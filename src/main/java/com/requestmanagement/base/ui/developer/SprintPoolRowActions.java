package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.messaging.MessageIndicatorIcon;
import com.requestmanagement.base.ui.messaging.RequestConversationDialog;
import com.requestmanagement.base.ui.messaging.RequestConversationView;
import com.requestmanagement.base.ui.shared.Toast;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/** Builds the note/customer-chat/claim action buttons for one Sprint-pool row. */
final class SprintPoolRowActions {

    private SprintPoolRowActions() {
    }

    static HorizontalLayout build(Workflow workflow, WorkflowRepository workflowRepository,
                                   UserRepository userRepository, NotificationRepository notificationRepository,
                                   RequestActivityRepository activityRepository,
                                   RequestMessageRepository messageRepository, AppUser currentDeveloper,
                                   Grid<Workflow> grid, Runnable onClaimed) {
        Button noteButton = new Button("Not", e -> new RequestConversationDialog(workflow.getRequest(),
                MessageChannel.INTERNAL, "Developer Notu", messageRepository, notificationRepository,
                userRepository, currentDeveloper, () -> grid.getDataProvider().refreshItem(workflow)).open());
        noteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        boolean unread = messageRepository.existsByRequestAndChannelAndReadFalseAndAuthorNot(
                workflow.getRequest(), MessageChannel.INTERNAL, currentDeveloper);

        Button viewCustomerChatButton = new Button("Görüşme", e -> new RequestConversationView(
                workflow.getRequest(), MessageChannel.CUSTOMER_PO, "Müşteri Görüşmesi", messageRepository).open());
        viewCustomerChatButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        Button claimButton = new Button("Üstlen", e -> {
            WorkflowActions.claim(workflow, currentDeveloper, workflowRepository, userRepository,
                    notificationRepository, activityRepository);
            Toast.show("Görevi üstlendiniz.");
            onClaimed.run();
        });
        claimButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        return new HorizontalLayout(claimButton, MessageIndicatorIcon.wrap(noteButton, unread),
                viewCustomerChatButton);
    }
}
