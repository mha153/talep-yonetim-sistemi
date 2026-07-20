package com.requestmanagement.base.ui.notifications;

import com.requestmanagement.base.model.AppNotification;
import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.UserRepository;

/** Creates {@link AppNotification} rows for the customer, the PO, or all developers. */
public final class NotificationCenter {

    private NotificationCenter() {
    }

    public static void notifyCustomer(NotificationRepository notificationRepository, Request request,
                                       AppUser actor, String message) {
        notify(notificationRepository, request.getCustomer(), request, actor, message);
    }

    public static void notifyProductOwner(NotificationRepository notificationRepository,
                                           UserRepository userRepository, Request request, AppUser actor,
                                           String message) {
        notifyByRole(notificationRepository, userRepository, Role.PRODUCT_OWNER, request, actor, message);
    }

    public static void notifyAllDevelopers(NotificationRepository notificationRepository,
                                            UserRepository userRepository, Request request, AppUser actor,
                                            String message) {
        notifyByRole(notificationRepository, userRepository, Role.DEVELOPER, request, actor, message);
    }

    private static void notifyByRole(NotificationRepository notificationRepository, UserRepository userRepository,
                                      Role role, Request request, AppUser actor, String message) {
        userRepository.findAll().stream()
                .filter(user -> role.equals(user.getRole()))
                .forEach(user -> notify(notificationRepository, user, request, actor, message));
    }

    private static void notify(NotificationRepository notificationRepository, AppUser recipient,
                                Request request, AppUser actor, String message) {
        AppNotification notification = new AppNotification();
        notification.setRecipient(recipient);
        notification.setRequest(request);
        notification.setActor(actor);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}
