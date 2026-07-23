package com.requestmanagement.base.ui.customer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.ui.messaging.MessageIndicatorIcon;
import com.requestmanagement.base.ui.messaging.RequestConversationDialog;
import com.requestmanagement.base.ui.shared.DeleteConfirmationDialog;
import com.requestmanagement.base.ui.shared.Toast;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/** Builds the message/cancel action buttons for one row of a customer's own request list. */
final class RequestHistoryRowActions {

    private RequestHistoryRowActions() {
    }

    static HorizontalLayout build(Request request, RequestRepository requestRepository,
                                   RequestActivityRepository activityRepository,
                                   RequestAttachmentRepository attachmentRepository,
                                   RequestMessageRepository messageRepository,
                                   NotificationRepository notificationRepository, UserRepository userRepository,
                                   AppUser currentUser, Runnable onChanged) {
        Button messageButton = new Button("Mesajlaş", e -> new RequestConversationDialog(
                request, MessageChannel.CUSTOMER_PO, "Müşteri Görüşmesi", messageRepository, notificationRepository,
                userRepository, currentUser, onChanged).open());
        messageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        boolean hasUnread = messageRepository.existsByRequestAndChannelAndReadFalseAndAuthorNot(
                request, MessageChannel.CUSTOMER_PO, currentUser);

        if (!RequestStatus.NEW.equals(request.getStatus())) {
            return new HorizontalLayout(MessageIndicatorIcon.wrap(messageButton, hasUnread));
        }
        Button cancelButton = new Button("İptal Et", e -> new DeleteConfirmationDialog(
                "Bu talebi silmek istediğinize emin misiniz?", () -> {
                    notificationRepository.deleteAll(notificationRepository.findByRequest(request));
                    messageRepository.deleteAll(messageRepository.findByRequest(request));
                    attachmentRepository.deleteAll(attachmentRepository.findByRequestOrderByCreatedAtAsc(request));
                    activityRepository.deleteAll(activityRepository.findByRequestOrderByCreatedAtAsc(request));
                    requestRepository.delete(request);
                    onChanged.run();
                    Toast.show("Talep silindi.");
                }).open());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        return new HorizontalLayout(MessageIndicatorIcon.wrap(messageButton, hasUnread), cancelButton);
    }
}
