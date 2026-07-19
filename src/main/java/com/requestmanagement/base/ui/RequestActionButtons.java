package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/** Builds the per-row "kebab" actions menu for {@link PendingRequestsGrid}. */
final class RequestActionButtons {

    private RequestActionButtons() {
    }

    static Component create(Request request, RequestRepository requestRepository,
                             PrioritizationRepository prioritizationRepository,
                             WorkflowRepository workflowRepository, UserRepository userRepository,
                             NotificationRepository notificationRepository,
                             RequestActivityRepository activityRepository,
                             RequestMessageRepository messageRepository, AppUser currentPo, Runnable onChange) {
        Button menuButton = new Button(new Icon(VaadinIcon.ELLIPSIS_DOTS_V));
        menuButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        ContextMenu contextMenu = new ContextMenu(menuButton);
        contextMenu.setOpenOnClick(true);

        contextMenu.addItem("Önceliklendir", e -> new PrioritizationDialog(request, prioritizationRepository,
                requestRepository, notificationRepository, activityRepository, currentPo, onChange).open());

        boolean hasWorkflow = workflowRepository.existsByRequest(request);
        if (RequestStatus.PRIORITIZED.equals(request.getStatus()) && !hasWorkflow) {
            contextMenu.addItem("Sprint'e Al", e -> {
                RequestStatusActions.convertToWorkflow(request, workflowRepository, userRepository,
                        notificationRepository, activityRepository, currentPo);
                Toast.show("Talep iş akışına aktarıldı.");
                onChange.run();
            });
        }

        contextMenu.addItem("Reddet", e -> {
            RequestStatusActions.reject(request, requestRepository, notificationRepository, activityRepository,
                    currentPo);
            onChange.run();
        });

        contextMenu.addItem("Müşteri ile Mesajlaş", e -> new RequestConversationDialog(request,
                MessageChannel.CUSTOMER_PO, "Müşteri Görüşmesi", messageRepository, notificationRepository,
                userRepository, currentPo, onChange).open());
        contextMenu.addItem("Developer Notu", e -> new RequestConversationDialog(request,
                MessageChannel.INTERNAL, "Developer Notu", messageRepository, notificationRepository,
                userRepository, currentPo, onChange).open());

        boolean hasUnread = messageRepository.existsByRequestAndReadFalseAndAuthorNot(request, currentPo);
        return MessageIndicatorIcon.wrap(menuButton, hasUnread);
    }
}
