package com.requestmanagement.base.security;

import com.requestmanagement.base.ui.LoginView;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            configurer.loginView(LoginView.class);
        });
        
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        // All passwords are 123 for now
        UserDetails customerUser = User.withUsername("customer").password("{noop}123").roles("CUSTOMER").build();
        UserDetails productOwnerUser = User.withUsername("po").password("{noop}123").roles("PRODUCT_OWNER").build();
        UserDetails developerUser = User.withUsername("developer").password("{noop}123").roles("DEVELOPER").build();

        return new InMemoryUserDetailsManager(customerUser, productOwnerUser, developerUser);
    }
}