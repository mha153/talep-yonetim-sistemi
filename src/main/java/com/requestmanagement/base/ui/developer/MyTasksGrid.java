package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.messaging.MessageIndicatorIcon;
import com.requestmanagement.base.ui.messaging.RequestConversationDialog;
import com.requestmanagement.base.ui.messaging.RequestConversationView;
import com.requestmanagement.base.ui.shared.RequestDetailsPanel;
import com.requestmanagement.base.ui.shared.Toast;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

/** A developer's own claimed workflow items, separate from the shared Sprint pool. */
class MyTasksGrid extends Grid<Workflow> {

    MyTasksGrid(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository,
                RequestActivityRepository activityRepository, RequestMessageRepository messageRepository,
                NotificationRepository notificationRepository, UserRepository userRepository,
                AppUser currentDeveloper) {
        super(Workflow.class, false);
        setSizeFull();

        addColumn(workflow -> workflow.getRequest().getRequestId()).setHeader("ID").setWidth("60px").setFlexGrow(0);
        addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(1);
        addColumn(workflow -> prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> String.valueOf(p.getPriorityScore()))
                .orElse("Belirlenmedi")).setHeader("Skor").setWidth("100px").setFlexGrow(0);
        addColumn(workflow -> workflow.getWorkflowStatus().displayLabel())
                .setHeader("Durum").setWidth("140px").setFlexGrow(0);
        addComponentColumn(workflow -> buildActions(workflow, workflowRepository, activityRepository,
                notificationRepository, userRepository, messageRepository, currentDeveloper))
                .setHeader("İşlem");

        setItemDetailsRenderer(new ComponentRenderer<>(
                workflow -> new RequestDetailsPanel(workflow.getRequest(), activityRepository)));
        setDetailsVisibleOnClick(true);

        setItems(workflowRepository.findByDeveloperAndWorkflowStatusNot(currentDeveloper, WorkflowStatus.DONE));
    }

    private HorizontalLayout buildActions(Workflow workflow, WorkflowRepository workflowRepository,
                                           RequestActivityRepository activityRepository,
                                           NotificationRepository notificationRepository,
                                           UserRepository userRepository, RequestMessageRepository messageRepository,
                                           AppUser currentDeveloper) {
        Button noteButton = new Button("Not", e -> new RequestConversationDialog(workflow.getRequest(),
                MessageChannel.INTERNAL, "Developer Notu", messageRepository, notificationRepository,
                userRepository, currentDeveloper, () -> getDataProvider().refreshItem(workflow)).open());
        noteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        boolean unread = messageRepository.existsByRequestAndChannelAndReadFalseAndAuthorNot(
                workflow.getRequest(), MessageChannel.INTERNAL, currentDeveloper);

        Button viewCustomerChatButton = new Button("Müşteri Görüşmesi", e -> new RequestConversationView(
                workflow.getRequest(), MessageChannel.CUSTOMER_PO, "Müşteri Görüşmesi", messageRepository).open());
        viewCustomerChatButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        Button completeButton = new Button("Tamamla", e -> {
            WorkflowActions.complete(workflow, currentDeveloper, workflowRepository, notificationRepository,
                    activityRepository);
            Toast.show("İş başarıyla tamamlandı!");
            setItems(workflowRepository.findByDeveloperAndWorkflowStatusNot(currentDeveloper, WorkflowStatus.DONE));
        });
        completeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        return new HorizontalLayout(completeButton, MessageIndicatorIcon.wrap(noteButton, unread),
                viewCustomerChatButton);
    }
}
