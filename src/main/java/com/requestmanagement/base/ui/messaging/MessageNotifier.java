package com.requestmanagement.base.ui.messaging;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.ui.notifications.NotificationCenter;

/** Notifies whoever is on the other side of a {@link RequestConversationDialog} channel. */
final class MessageNotifier {

    private MessageNotifier() {
    }

    static void notifyOtherParty(Request request, MessageChannel channel, AppUser sender,
                                  NotificationRepository notificationRepository, UserRepository userRepository) {
        String text = "bir mesaj gönderdi: " + request.getTitle();
        if (channel == MessageChannel.CUSTOMER_PO) {
            if (Role.CUSTOMER.equals(sender.getRole())) {
                NotificationCenter.notifyProductOwner(notificationRepository, userRepository, request, sender, text);
            } else {
                NotificationCenter.notifyCustomer(notificationRepository, request, sender, text);
            }
        } else if (Role.DEVELOPER.equals(sender.getRole())) {
            NotificationCenter.notifyProductOwner(notificationRepository, userRepository, request, sender, text);
        } else {
            NotificationCenter.notifyAllDevelopers(notificationRepository, userRepository, request, sender, text);
        }
    }
}
