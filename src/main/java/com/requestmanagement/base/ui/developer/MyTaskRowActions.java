package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/** Builds the status-dependent action buttons for one row of a developer's own task list. */
final class MyTaskRowActions {

    private MyTaskRowActions() {
    }

    static HorizontalLayout build(Workflow workflow, WorkflowRepository workflowRepository,
                                   UserRepository userRepository, NotificationRepository notificationRepository,
                                   RequestActivityRepository activityRepository,
                                   RequestMessageRepository messageRepository, AppUser currentDeveloper,
                                   Runnable onChanged) {
        Button noteButton = new Button("Not", e -> new RequestConversationDialog(workflow.getRequest(),
                MessageChannel.INTERNAL, "Developer Notu", messageRepository, notificationRepository,
                userRepository, currentDeveloper, onChanged).open());
        noteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        boolean unread = messageRepository.existsByRequestAndChannelAndReadFalseAndAuthorNot(
                workflow.getRequest(), MessageChannel.INTERNAL, currentDeveloper);

        Button chatButton = new Button("Görüşme", e -> new RequestConversationView(
                workflow.getRequest(), MessageChannel.CUSTOMER_PO, "Müşteri Görüşmesi", messageRepository).open());
        chatButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        HorizontalLayout actions = new HorizontalLayout();
        if (WorkflowStatus.TESTING.equals(workflow.getWorkflowStatus())) {
            Button completeButton = new Button("Tamamla", e -> {
                WorkflowActions.complete(workflow, currentDeveloper, workflowRepository, notificationRepository,
                        activityRepository);
                Toast.show("İş başarıyla tamamlandı!");
                onChanged.run();
            });
            completeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
            actions.add(completeButton);
        } else {
            Button testButton = new Button("Teste Al", e -> {
                WorkflowActions.startTesting(workflow, currentDeveloper, workflowRepository, userRepository,
                        notificationRepository, activityRepository);
                Toast.show("İş test aşamasına alındı.");
                onChanged.run();
            });
            testButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            Button releaseButton = new Button("Havuza Bırak", e -> {
                WorkflowActions.release(workflow, currentDeveloper, workflowRepository, userRepository,
                        notificationRepository, activityRepository);
                Toast.show("Görev havuza geri bırakıldı.");
                onChanged.run();
            });
            releaseButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL,
                    ButtonVariant.LUMO_TERTIARY);
            actions.add(testButton, releaseButton);
        }
        actions.add(MessageIndicatorIcon.wrap(noteButton, unread), chatButton);
        return actions;
    }
}
