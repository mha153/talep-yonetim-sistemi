package com.requestmanagement.base;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the three demo login users into MUSTAFA_USERS and, if present, migrates
 * MUSTAFA_REQUESTS_LEGACY into the normalized schema via {@link LegacyRequestMigrator}.
 */
@Component
public class DataMigrationRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final LegacyRequestMigrator legacyRequestMigrator;

    public DataMigrationRunner(JdbcTemplate jdbcTemplate, UserRepository userRepository,
                                RequestRepository requestRepository,
                                PrioritizationRepository prioritizationRepository,
                                WorkflowRepository workflowRepository) {
        this.userRepository = userRepository;
        this.legacyRequestMigrator = new LegacyRequestMigrator(
                jdbcTemplate, userRepository, requestRepository, prioritizationRepository, workflowRepository);
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedLoginUsers();
        legacyRequestMigrator.migrateIfPresent();
    }

    private void seedLoginUsers() {
        seedOrRename("musteri@talep.local", "customer@requestmanagement.local", "Müşteri Kullanıcı", Role.CUSTOMER);
        seedOrRename("po@talep.local", "po@requestmanagement.local", "Ürün Sorumlusu", Role.PRODUCT_OWNER);
        seedOrRename("yazilimci@talep.local", "developer@requestmanagement.local", "Yazılımcı", Role.DEVELOPER);
    }

    private void seedOrRename(String oldEmail, String newEmail, String nameSurname, Role role) {
        if (userRepository.findByEmail(newEmail).isPresent()) {
            return;
        }
        AppUser user = userRepository.findByEmail(oldEmail).orElseGet(AppUser::new);
        user.setEmail(newEmail);
        user.setNameSurname(nameSurname);
        user.setRole(role);
        userRepository.save(user);
    }
}
