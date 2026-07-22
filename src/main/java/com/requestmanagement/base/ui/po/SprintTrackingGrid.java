package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.developer.SprintPoolSorter;
import com.requestmanagement.base.ui.messaging.MessageIndicatorIcon;
import com.requestmanagement.base.ui.messaging.RequestConversationDialog;
import com.requestmanagement.base.ui.shared.RequestDetailsPanel;
import com.requestmanagement.base.ui.shared.RequestSearchFilter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;

/** PO-facing, read-only view of workflow items that are not yet done. */
class SprintTrackingGrid extends Grid<Workflow> {

    private final transient WorkflowRepository workflowRepository;
    private final transient PrioritizationRepository prioritizationRepository;
    private String searchText = "";

    SprintTrackingGrid(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository,
                        RequestActivityRepository activityRepository, RequestAttachmentRepository attachmentRepository,
                        RequestMessageRepository messageRepository, NotificationRepository notificationRepository,
                        UserRepository userRepository, AppUser currentPo) {
        super(Workflow.class, false);
        this.workflowRepository = workflowRepository;
        this.prioritizationRepository = prioritizationRepository;
        setSizeFull();

        addColumn(workflow -> workflow.getRequest().getRequestId()).setHeader("ID").setWidth("60px").setFlexGrow(0);
        addColumn(workflow -> workflow.getRequest().getCustomer().getNameSurname()).setHeader("Müşteri").setFlexGrow(1);
        addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(1);
        addColumn(workflow -> prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> String.valueOf(p.getPriorityScore()))
                .orElse("Belirlenmedi")).setHeader("Skor").setWidth("100px").setFlexGrow(0);
        addColumn(workflow -> workflow.getWorkflowStatus().displayLabel())
                .setHeader("Durum").setWidth("140px").setFlexGrow(0);
        addColumn(workflow -> workflow.getDeveloper() == null ? "Atanmadı" : workflow.getDeveloper().getNameSurname())
                .setHeader("Üstlenen").setWidth("140px").setFlexGrow(0);
        addComponentColumn(workflow -> buildCommunicationButtons(workflow, messageRepository,
                notificationRepository, userRepository, currentPo))
                .setHeader("İletişim").setWidth("140px").setFlexGrow(0);

        setItemDetailsRenderer(new ComponentRenderer<>(
                workflow -> new RequestDetailsPanel(workflow.getRequest(), activityRepository, attachmentRepository)));
        setDetailsVisibleOnClick(true);

        refresh();
    }

    void search(String text) {
        this.searchText = text;
        refresh();
    }

    void refresh() {
        List<Workflow> workflows = workflowRepository.findByWorkflowStatusNot(WorkflowStatus.DONE);
        workflows = RequestSearchFilter.apply(workflows, searchText,
                w -> w.getRequest().getCustomer().getNameSurname(), w -> w.getRequest().getTitle());
        setItems(SprintPoolSorter.byScoreDescending(workflows, prioritizationRepository));
    }

    private HorizontalLayout buildCommunicationButtons(Workflow workflow, RequestMessageRepository messageRepository,
                                                         NotificationRepository notificationRepository,
                                                         UserRepository userRepository, AppUser currentPo) {
        Button messageButton = new Button("Mesaj", e -> new RequestConversationDialog(workflow.getRequest(),
                MessageChannel.CUSTOMER_PO, "Müşteri Görüşmesi", messageRepository, notificationRepository,
                userRepository, currentPo, () -> getDataProvider().refreshItem(workflow)).open());
        messageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        boolean messageUnread = messageRepository.existsByRequestAndChannelAndReadFalseAndAuthorNot(
                workflow.getRequest(), MessageChannel.CUSTOMER_PO, currentPo);

        Button noteButton = new Button("Not", e -> new RequestConversationDialog(workflow.getRequest(),
                MessageChannel.INTERNAL, "Developer Notu", messageRepository, notificationRepository,
                userRepository, currentPo, () -> getDataProvider().refreshItem(workflow)).open());
        noteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        boolean noteUnread = messageRepository.existsByRequestAndChannelAndReadFalseAndAuthorNot(
                workflow.getRequest(), MessageChannel.INTERNAL, currentPo);

        return new HorizontalLayout(MessageIndicatorIcon.wrap(messageButton, messageUnread),
                MessageIndicatorIcon.wrap(noteButton, noteUnread));
    }
}
