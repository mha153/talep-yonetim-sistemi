package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.repository.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import org.springframework.security.crypto.password.PasswordEncoder;

/** The password-change section of {@link ProfileView}. */
class PasswordChangeForm extends VerticalLayout {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 6;

    PasswordChangeForm(AppUser currentUser, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        setPadding(false);

        PasswordField currentPasswordField = new PasswordField("Mevcut Şifre");
        PasswordField newPasswordField = new PasswordField("Yeni Şifre");
        newPasswordField.setHelperText(MIN_LENGTH + "-" + MAX_LENGTH + " karakter arasında olmalı");
        newPasswordField.setMaxLength(MAX_LENGTH);
        PasswordField confirmPasswordField = new PasswordField("Yeni Şifre (Tekrar)");
        currentPasswordField.setWidth("320px");
        newPasswordField.setWidth("320px");
        confirmPasswordField.setWidth("320px");

        Button changeButton = new Button("Şifreyi Değiştir", e -> {
            if (!passwordEncoder.matches(currentPasswordField.getValue(), currentUser.getPassword())) {
                Toast.show("Mevcut şifre yanlış.");
                return;
            }
            String newPassword = newPasswordField.getValue();
            if (newPassword.length() < MIN_LENGTH || newPassword.length() > MAX_LENGTH) {
                Toast.show("Yeni şifre " + MIN_LENGTH + "-" + MAX_LENGTH + " karakter arasında olmalı.");
                return;
            }
            if (!newPassword.equals(confirmPasswordField.getValue())) {
                Toast.show("Yeni şifreler eşleşmiyor.");
                return;
            }
            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(currentUser);
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            Toast.show("Şifre güncellendi.");
        });
        changeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);

        add(new H4("Şifre Değiştir"), currentPasswordField, newPasswordField, confirmPasswordField, changeButton);
    }
}
