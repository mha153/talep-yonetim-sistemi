package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.EmailChangeRequest;
import com.requestmanagement.base.model.RegistrationRequest;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.EmailChangeRequestRepository;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RegistrationRequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.ui.notifications.NotificationCenter;
import com.requestmanagement.base.ui.shared.Toast;

/** Approve/reject logic for the rows shown in {@link PendingAccountRequestsGrid}. */
final class PendingAccountRequestActions {

    private PendingAccountRequestActions() {
    }

    static void approveRegistration(RegistrationRequest registration,
                                     RegistrationRequestRepository registrationRequestRepository,
                                     UserRepository userRepository, Runnable onChanged) {
        String requestedEmail = registration.getRequestedEmail();
        if (userRepository.findByEmail(requestedEmail).isPresent()) {
            registrationRequestRepository.delete(registration);
            onChanged.run();
            Toast.show("Bu kullanıcı adı artık kullanımda; kayıt talebi reddedildi.");
            return;
        }
        AppUser user = new AppUser();
        user.setEmail(requestedEmail);
        user.setNameSurname(registration.getNameSurname());
        user.setRole(Role.CUSTOMER);
        user.setPassword(registration.getPasswordHash());
        userRepository.save(user);
        registrationRequestRepository.delete(registration);
        onChanged.run();
        Toast.show("Kayıt onaylandı, kullanıcı giriş yapabilir.");
    }

    static void rejectRegistration(RegistrationRequest registration,
                                    RegistrationRequestRepository registrationRequestRepository,
                                    Runnable onChanged) {
        registrationRequestRepository.delete(registration);
        onChanged.run();
        Toast.show("Kayıt talebi reddedildi.");
    }

    static void approveEmailChange(EmailChangeRequest changeRequest,
                                    EmailChangeRequestRepository emailChangeRequestRepository,
                                    UserRepository userRepository, NotificationRepository notificationRepository,
                                    AppUser currentPo, Runnable onChanged) {
        String requestedEmail = changeRequest.getRequestedEmail();
        if (userRepository.findByEmail(requestedEmail).isPresent()) {
            emailChangeRequestRepository.delete(changeRequest);
            onChanged.run();
            Toast.show("Bu kullanıcı adı artık başkası tarafından kullanılıyor; talep reddedildi.");
            return;
        }
        AppUser user = changeRequest.getUser();
        user.setEmail(requestedEmail);
        userRepository.save(user);
        emailChangeRequestRepository.delete(changeRequest);
        NotificationCenter.notifyUserAccountEvent(notificationRepository, user, currentPo,
                "e-posta değişikliğinizi onayladı. Yeni kullanıcı adınızla tekrar giriş yapmanız gerekiyor: "
                        + requestedEmail);
        onChanged.run();
        Toast.show("E-posta değişikliği onaylandı.");
    }

    static void rejectEmailChange(EmailChangeRequest changeRequest,
                                   EmailChangeRequestRepository emailChangeRequestRepository,
                                   NotificationRepository notificationRepository, AppUser currentPo,
                                   Runnable onChanged) {
        AppUser user = changeRequest.getUser();
        emailChangeRequestRepository.delete(changeRequest);
        NotificationCenter.notifyUserAccountEvent(notificationRepository, user, currentPo,
                "e-posta değişiklik talebinizi reddetti.");
        onChanged.run();
        Toast.show("Talep reddedildi.");
    }
}
