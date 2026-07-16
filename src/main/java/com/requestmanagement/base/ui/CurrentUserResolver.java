package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.UserRepository;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/** Resolves the AppUser record matching the currently authenticated principal. */
final class CurrentUserResolver {

    private static final String EMAIL_DOMAIN = "@requestmanagement.local";

    private CurrentUserResolver() {
    }

    static Optional<AppUser> find(UserRepository userRepository, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName() + EMAIL_DOMAIN);
    }

    static AppUser findOrCreate(UserRepository userRepository, Authentication authentication, Role fallbackRole) {
        return find(userRepository, authentication).orElseGet(() -> {
            AppUser newUser = new AppUser();
            newUser.setEmail(authentication.getName() + EMAIL_DOMAIN);
            newUser.setNameSurname(authentication.getName());
            newUser.setRole(fallbackRole);
            return userRepository.save(newUser);
        });
    }
}
