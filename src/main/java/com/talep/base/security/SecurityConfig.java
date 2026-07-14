package com.talep.base.security;

import com.talep.base.ui.LoginView;
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
        // Şifrelerin hepsi şimdilik 123
        UserDetails musteri = User.withUsername("musteri").password("{noop}123").roles("MUSTERI").build();
        UserDetails po = User.withUsername("po").password("{noop}123").roles("PO").build();
        UserDetails yazilimci = User.withUsername("yazilimci").password("{noop}123").roles("YAZILIMCI").build();
        
        return new InMemoryUserDetailsManager(musteri, po, yazilimci);
    }
}