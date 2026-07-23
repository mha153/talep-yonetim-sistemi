package com.requestmanagement.base.ui.login;

import com.requestmanagement.base.model.RegistrationRequest;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RegistrationRequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.ui.notifications.NotificationCenter;
import com.requestmanagement.base.ui.shared.BlankLayout;
import com.requestmanagement.base.ui.shared.Toast;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Public self-registration screen; submissions wait for PO approval before an account is created. */
@Route(value = "register", layout = BlankLayout.class)
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private static final String EMAIL_DOMAIN = "@requestmanagement.local";
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 6;

    public RegisterView(UserRepository userRepository, RegistrationRequestRepository registrationRequestRepository,
                         NotificationRepository notificationRepository, PasswordEncoder passwordEncoder) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        TextField usernameField = new TextField("Kullanıcı Adı");
        TextField nameField = new TextField("Ad Soyad");
        PasswordField passwordField = new PasswordField("Şifre");
        passwordField.setHelperText(MIN_LENGTH + "-" + MAX_LENGTH + " karakter arasında olmalı");
        passwordField.setMaxLength(MAX_LENGTH);
        PasswordField confirmField = new PasswordField("Şifre (Tekrar)");

        Button registerButton = new Button("Kayıt Ol", e -> register(userRepository, registrationRequestRepository,
                notificationRepository, passwordEncoder, usernameField, nameField, passwordField, confirmField));
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        RouterLink loginLink = new RouterLink("Zaten hesabım var, giriş yap", LoginView.class);
        loginLink.getStyle().set("text-decoration", "none");

        add(new H1("Talep Yönetim Sistemi"), usernameField, nameField, passwordField, confirmField, registerButton,
                loginLink);
    }

    private void register(UserRepository userRepository, RegistrationRequestRepository registrationRequestRepository,
                           NotificationRepository notificationRepository, PasswordEncoder passwordEncoder,
                           TextField usernameField, TextField nameField, PasswordField passwordField,
                           PasswordField confirmField) {
        if (usernameField.isEmpty() || nameField.isEmpty() || passwordField.isEmpty()) {
            Toast.show("Lütfen tüm alanları doldurun.");
            return;
        }
        String password = passwordField.getValue();
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            Toast.show("Şifre " + MIN_LENGTH + "-" + MAX_LENGTH + " karakter arasında olmalı.");
            return;
        }
        if (!password.equals(confirmField.getValue())) {
            Toast.show("Şifreler eşleşmiyor.");
            return;
        }
        String email = usernameField.getValue() + EMAIL_DOMAIN;
        if (userRepository.findByEmail(email).isPresent()) {
            Toast.show("Bu kullanıcı adı zaten kayıtlı.");
            return;
        }
        if (registrationRequestRepository.existsByRequestedEmail(email)) {
            Toast.show("Bu kullanıcı adı için zaten bekleyen bir kayıt talebi var.");
            return;
        }

        RegistrationRequest registration = new RegistrationRequest();
        registration.setRequestedEmail(email);
        registration.setNameSurname(nameField.getValue());
        registration.setPasswordHash(passwordEncoder.encode(password));
        registrationRequestRepository.save(registration);

        NotificationCenter.notifyProductOwnerAccountEvent(notificationRepository, userRepository, null,
                "yeni bir kayıt talebi oluşturdu: " + nameField.getValue() + " (" + email + ")");

        Toast.show("Kaydınız alındı. Ürün Sorumlusu onayladıktan sonra giriş yapabilirsiniz.");
        getUI().ifPresent(ui -> ui.navigate(LoginView.class));
    }
}
