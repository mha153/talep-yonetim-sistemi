package com.requestmanagement.base;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads MUSTAFA_REQUESTS_LEGACY (if present) and migrates it into the new
 * users/requests/prioritizations/workflows schema via {@link LegacyRowConverter}.
 * Never writes to or drops the legacy table itself.
 */
class LegacyRequestMigrator {

    private static final Logger log = LoggerFactory.getLogger(LegacyRequestMigrator.class);

    private final JdbcTemplate jdbcTemplate;
    private final RequestRepository requestRepository;
    private final LegacyRowConverter rowConverter;

    LegacyRequestMigrator(JdbcTemplate jdbcTemplate, UserRepository userRepository,
                           RequestRepository requestRepository, PrioritizationRepository prioritizationRepository,
                           WorkflowRepository workflowRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.requestRepository = requestRepository;
        this.rowConverter = new LegacyRowConverter(
                userRepository, requestRepository, prioritizationRepository, workflowRepository);
    }

    void migrateIfPresent() {
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
        legacyRows.forEach(row -> rowConverter.convert(row, customersByName));
        log.info("Eski veri taşıma tamamlandı.");
    }
}
