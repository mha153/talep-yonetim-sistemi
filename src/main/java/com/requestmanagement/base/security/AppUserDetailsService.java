package com.requestmanagement.base.security;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/** Loads login credentials for Spring Security directly from {@link AppUser} rows in the database. */
@Component
class AppUserDetailsService implements UserDetailsService {

    private static final String EMAIL_DOMAIN = "@requestmanagement.local";

    private final UserRepository userRepository;

    AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = userRepository.findByEmail(username + EMAIL_DOMAIN)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.withUsername(username)
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
