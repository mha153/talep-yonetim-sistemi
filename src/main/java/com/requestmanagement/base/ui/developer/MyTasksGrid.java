package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.RequestDetailsPanel;
import com.requestmanagement.base.ui.shared.RequestSearchFilter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.ComponentRenderer;

/** A developer's own claimed workflow items, separate from the shared Sprint pool. */
class MyTasksGrid extends Grid<Workflow> {

    private final transient WorkflowRepository workflowRepository;
    private final transient AppUser currentDeveloper;
    private String searchText = "";

    MyTasksGrid(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository,
                RequestActivityRepository activityRepository, RequestAttachmentRepository attachmentRepository,
                RequestMessageRepository messageRepository, NotificationRepository notificationRepository,
                UserRepository userRepository, AppUser currentDeveloper) {
        super(Workflow.class, false);
        this.workflowRepository = workflowRepository;
        this.currentDeveloper = currentDeveloper;
        setSizeFull();

        addColumn(workflow -> workflow.getRequest().getRequestId()).setHeader("ID").setWidth("60px").setFlexGrow(0);
        addColumn(workflow -> workflow.getRequest().getCustomer().getNameSurname())
                .setHeader("Müşteri").setFlexGrow(1);
        addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(1);
        addColumn(workflow -> prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> String.valueOf(p.getPriorityScore()))
                .orElse("Belirlenmedi")).setHeader("Skor").setWidth("100px").setFlexGrow(0);
        addColumn(workflow -> workflow.getWorkflowStatus().displayLabel())
                .setHeader("Durum").setWidth("140px").setFlexGrow(0);
        addComponentColumn(workflow -> MyTaskRowActions.build(workflow, workflowRepository, userRepository,
                notificationRepository, activityRepository, messageRepository, currentDeveloper, this::refresh))
                .setHeader("İşlem").setWidth("380px").setFlexGrow(0);

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
        var workflows = workflowRepository.findByDeveloperAndWorkflowStatusNot(currentDeveloper, WorkflowStatus.DONE);
        setItems(RequestSearchFilter.apply(workflows, searchText,
                w -> w.getRequest().getCustomer().getNameSurname(), w -> w.getRequest().getTitle()));
    }
}
