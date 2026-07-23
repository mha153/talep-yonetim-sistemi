package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.EmailChangeRequestRepository;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.Toast;

/** Delete/deactivate logic for the rows shown in {@link UserManagementGrid}. */
final class UserManagementActions {

    private UserManagementActions() {
    }

    static void delete(AppUser user, UserRepository userRepository, RequestRepository requestRepository,
                        WorkflowRepository workflowRepository, NotificationRepository notificationRepository,
                        RequestMessageRepository messageRepository,
                        EmailChangeRequestRepository emailChangeRequestRepository, Runnable onChanged) {
        if (requestRepository.existsByCustomer(user) || workflowRepository.existsByDeveloper(user)
                || notificationRepository.existsByRecipient(user) || notificationRepository.existsByActor(user)
                || messageRepository.existsByAuthor(user) || emailChangeRequestRepository.existsByUser(user)) {
            Toast.show("Bu kullanıcı silinemez: bağlı talep, görev, bildirim, mesaj veya bekleyen talep var.");
            return;
        }
        userRepository.delete(user);
        onChanged.run();
        Toast.show("Kullanıcı silindi.");
    }

    static void toggleActive(AppUser user, UserRepository userRepository, WorkflowRepository workflowRepository,
                              Runnable onChanged) {
        if (user.isActive()) {
            if (Role.DEVELOPER.equals(user.getRole())
                    && workflowRepository.existsByDeveloperAndWorkflowStatusNot(user, WorkflowStatus.DONE)) {
                Toast.show("Bu developer'ın devam eden bir görevi var; önce görevi başkasına devretmesi gerekir.");
                return;
            }
            user.setActive(false);
            userRepository.save(user);
            onChanged.run();
            Toast.show("Kullanıcı pasifleştirildi.");
            return;
        }
        user.setActive(true);
        userRepository.save(user);
        onChanged.run();
        Toast.show("Kullanıcı aktifleştirildi.");
    }
}
