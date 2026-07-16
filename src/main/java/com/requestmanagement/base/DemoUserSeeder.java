package com.requestmanagement.base;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Seeds the demo login users (3 customers, 1 PO, 3 developers) into MUSTAFA_USERS. */
@Component
public class DemoUserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;

    public DemoUserSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedIfMissing("customer1@requestmanagement.local", "Zeynep Kaya", Role.CUSTOMER);
        seedIfMissing("customer2@requestmanagement.local", "Ahmet Yılmaz", Role.CUSTOMER);
        seedIfMissing("customer3@requestmanagement.local", "Mehmet Demir", Role.CUSTOMER);
        seedIfMissing("po@requestmanagement.local", "Ürün Sorumlusu", Role.PRODUCT_OWNER);
        seedIfMissing("developer1@requestmanagement.local", "Emre Kara", Role.DEVELOPER);
        seedIfMissing("developer2@requestmanagement.local", "Deniz Polat", Role.DEVELOPER);
        seedIfMissing("developer3@requestmanagement.local", "Gizem Aksoy", Role.DEVELOPER);
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
}
