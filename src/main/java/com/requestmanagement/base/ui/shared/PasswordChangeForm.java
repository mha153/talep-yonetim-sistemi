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

    PasswordChangeForm(AppUser currentUser, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        setPadding(false);

        PasswordField currentPasswordField = new PasswordField("Mevcut Şifre");
        PasswordField newPasswordField = new PasswordField("Yeni Şifre");
        PasswordField confirmPasswordField = new PasswordField("Yeni Şifre (Tekrar)");
        currentPasswordField.setWidth("320px");
        newPasswordField.setWidth("320px");
        confirmPasswordField.setWidth("320px");

        Button changeButton = new Button("Şifreyi Değiştir", e -> {
            if (!passwordEncoder.matches(currentPasswordField.getValue(), currentUser.getPassword())) {
                Toast.show("Mevcut şifre yanlış.");
                return;
            }
            if (newPasswordField.isEmpty() || !newPasswordField.getValue().equals(confirmPasswordField.getValue())) {
                Toast.show("Yeni şifreler eşleşmiyor veya boş.");
                return;
            }
            currentUser.setPassword(passwordEncoder.encode(newPasswordField.getValue()));
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
