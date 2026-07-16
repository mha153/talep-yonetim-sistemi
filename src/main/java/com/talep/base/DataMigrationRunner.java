package com.talep.base;

import com.talep.base.model.AppUser;
import com.talep.base.model.Prioritization;
import com.talep.base.model.Request;
import com.talep.base.model.RequestStatus;
import com.talep.base.model.Role;
import com.talep.base.model.Workflow;
import com.talep.base.model.WorkflowStatus;
import com.talep.base.repository.PrioritizationRepository;
import com.talep.base.repository.RequestRepository;
import com.talep.base.repository.UserRepository;
import com.talep.base.repository.WorkflowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Giriş kullanıcılarını (musteri/po/yazilimci) MUSTAFA_USERS tablosuna seed eder ve,
 * varsa, MUSTAFA_REQUESTS_LEGACY tablosundaki eski (denormalize) talepleri yeni
 * users/requests/prioritizations/workflows şemasına taşır. MUSTAFA_REQUESTS_LEGACY
 * elle oluşturulur (bkz. proje kökündeki migration talimatı); bu runner sadece okur,
 * hiçbir tabloyu silmez veya yeniden adlandırmaz.
 */
@Component
public class DataMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final PrioritizationRepository prioritizationRepository;
    private final WorkflowRepository workflowRepository;

    public DataMigrationRunner(JdbcTemplate jdbcTemplate, UserRepository userRepository,
                                RequestRepository requestRepository,
                                PrioritizationRepository prioritizationRepository,
                                WorkflowRepository workflowRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.prioritizationRepository = prioritizationRepository;
        this.workflowRepository = workflowRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedLoginUsers();
        migrateLegacyRequestsIfPresent();
    }

    private void seedLoginUsers() {
        seedIfMissing("musteri@talep.local", "Müşteri Kullanıcı", Role.CUSTOMER);
        seedIfMissing("po@talep.local", "Ürün Sorumlusu", Role.PRODUCT_OWNER);
        seedIfMissing("yazilimci@talep.local", "Yazılımcı", Role.DEVELOPER);
    }

    private void seedIfMissing(String email, String nameSurname, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setNameSurname(nameSurname);
        user.setRole(role);
        userRepository.save(user);
    }

    private void migrateLegacyRequestsIfPresent() {
        if (!requestRepository.findAll().isEmpty()) {
            return;
        }

        List<Map<String, Object>> legacyRows;
        try {
            legacyRows = jdbcTemplate.queryForList("SELECT * FROM MUSTAFA_REQUESTS_LEGACY");
        } catch (Exception ex) {
            log.info("MUSTAFA_REQUESTS_LEGACY bulunamadı, eski veri taşıma atlanıyor.");
            return;
        }

        log.info("MUSTAFA_REQUESTS_LEGACY içinde {} kayıt bulundu, yeni şemaya taşınıyor...", legacyRows.size());

        Map<String, AppUser> customersByName = new HashMap<>();

        for (Map<String, Object> row : legacyRows) {
            String musteriAdi = str(row.get("MUSTERI"));
            String baslik = str(row.get("BASLIK"));
            String aciklama = str(row.get("ACIKLAMA"));
            String durum = str(row.get("DURUM"));
            Number aciliyet = (Number) row.get("ACILIYET");
            Number etki = (Number) row.get("ETKI");

            if (musteriAdi == null || musteriAdi.isBlank()) {
                musteriAdi = "Bilinmeyen Müşteri";
            }
            String musteriAdiFinal = musteriAdi;

            AppUser customer = customersByName.computeIfAbsent(musteriAdiFinal, name -> {
                AppUser u = new AppUser();
                u.setNameSurname(name);
                u.setEmail(slugify(name) + "@migrated.local");
                u.setRole(Role.CUSTOMER);
                return userRepository.save(u);
            });

            Request request = new Request();
            request.setCustomer(customer);
            request.setTitle(baslik != null && !baslik.isBlank() ? baslik : "(başlıksız)");
            request.setDescription(aciklama != null && !aciklama.isBlank() ? aciklama : "(açıklama girilmemiş)");
            request.setStatus(mapStatus(durum));
            request = requestRepository.save(request);

            if (aciliyet != null && etki != null) {
                Prioritization p = new Prioritization();
                p.setRequest(request);
                p.setUrgency(aciliyet.intValue());
                p.setImpact(etki.intValue());
                p.setPriorityScore(aciliyet.intValue() * etki.intValue());
                prioritizationRepository.save(p);
            }

            WorkflowStatus workflowStatus = mapWorkflowStatus(durum);
            if (workflowStatus != null) {
                Workflow workflow = new Workflow();
                workflow.setRequest(request);
                workflow.setWorkflowStatus(workflowStatus);
                workflowRepository.save(workflow);
            }
        }

        log.info("Eski veri taşıma tamamlandı.");
    }

    private RequestStatus mapStatus(String durum) {
        if (durum == null) {
            return RequestStatus.NEW;
        }
        return switch (durum.trim()) {
            case "REDDEDİLDİ", "İPTAL EDİLDİ" -> RequestStatus.REJECTED;
            case "ÖNCELİKLENDİRİLDİ", "YAZILIMCIYA ATANDI", "SPRINT", "TAMAMLANDI" -> RequestStatus.PRIORITIZED;
            default -> RequestStatus.NEW;
        };
    }

    private WorkflowStatus mapWorkflowStatus(String durum) {
        if (durum == null) {
            return null;
        }
        return switch (durum.trim()) {
            case "YAZILIMCIYA ATANDI" -> WorkflowStatus.BACKLOG;
            case "SPRINT" -> WorkflowStatus.IN_PROGRESS;
            case "TAMAMLANDI" -> WorkflowStatus.DONE;
            default -> null;
        };
    }

    private String str(Object value) {
        return value == null ? null : value.toString();
    }

    private String slugify(String name) {
        return name.trim().toLowerCase()
                .replace("ı", "i").replace("ş", "s").replace("ğ", "g")
                .replace("ü", "u").replace("ö", "o").replace("ç", "c")
                .replaceAll("[^a-z0-9]+", ".");
    }
}
