package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.EmailChangeRequest;
import com.requestmanagement.base.repository.EmailChangeRequestRepository;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.ui.notifications.NotificationCenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;

/** Lets a user submit a new login username; the change only takes effect once the PO approves it. */
public class EmailChangeDialog extends Dialog {

    private static final String EMAIL_DOMAIN = "@requestmanagement.local";

    public EmailChangeDialog(AppUser currentUser, UserRepository userRepository,
                              EmailChangeRequestRepository emailChangeRequestRepository,
                              NotificationRepository notificationRepository) {
        setHeaderTitle("E-posta / Kullanıcı Adı Değiştir");

        TextField usernameField = new TextField("Yeni Kullanıcı Adı");
        usernameField.setHelperText("Sisteme \"kullaniciadi" + EMAIL_DOMAIN + "\" şeklinde kaydedilir.");
        usernameField.setWidthFull();

        Button submitButton = new Button("Talep Gönder", e -> submit(usernameField.getValue(), currentUser,
                userRepository, emailChangeRequestRepository, notificationRepository));
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("İptal", e -> close());

        add(usernameField);
        getFooter().add(submitButton, cancelButton);
    }

    private void submit(String username, AppUser currentUser, UserRepository userRepository,
                         EmailChangeRequestRepository emailChangeRequestRepository,
                         NotificationRepository notificationRepository) {
        if (username == null || username.isBlank()) {
            Toast.show("Lütfen bir kullanıcı adı girin.");
            return;
        }
        String requestedEmail = username.trim() + EMAIL_DOMAIN;
        if (requestedEmail.equals(currentUser.getEmail())) {
            Toast.show("Bu zaten mevcut kullanıcı adınız.");
            return;
        }
        if (userRepository.findByEmail(requestedEmail).isPresent()) {
            Toast.show("Bu kullanıcı adı zaten kullanımda.");
            return;
        }
        if (emailChangeRequestRepository.existsByUser(currentUser)) {
            Toast.show("Zaten bekleyen bir talebiniz var.");
            return;
        }

        EmailChangeRequest changeRequest = new EmailChangeRequest();
        changeRequest.setUser(currentUser);
        changeRequest.setRequestedEmail(requestedEmail);
        emailChangeRequestRepository.save(changeRequest);

        NotificationCenter.notifyProductOwnerAccountEvent(notificationRepository, userRepository, currentUser,
                "e-posta değişikliği talep etti: " + requestedEmail);

        Toast.show("Talebiniz PO onayına gönderildi.");
        close();
    }
}
