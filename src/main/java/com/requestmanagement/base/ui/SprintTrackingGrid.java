package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;

/** PO-facing, read-only view of workflow items that are not yet done. */
class SprintTrackingGrid extends Grid<Workflow> {

    SprintTrackingGrid(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository) {
        super(Workflow.class, false);
        setSizeFull();

        addColumn(Workflow::getTaskId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        addColumn(workflow -> workflow.getRequest().getCustomer().getNameSurname()).setHeader("Müşteri").setFlexGrow(1);
        addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(1);
        addColumn(workflow -> prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> String.valueOf(p.getPriorityScore()))
                .orElse("Belirlenmedi")).setHeader("Skor").setWidth("100px").setFlexGrow(0);
        addColumn(workflow -> workflow.getWorkflowStatus().displayLabel())
                .setHeader("Durum").setWidth("140px").setFlexGrow(0);
        addColumn(workflow -> workflow.getDeveloper() == null ? "Atanmadı" : workflow.getDeveloper().getNameSurname())
                .setHeader("Üstlenen").setWidth("140px").setFlexGrow(0);

        setItemDetailsRenderer(new ComponentRenderer<>(workflow -> {
            Span description = new Span("Açıklama: " + workflow.getRequest().getDescription());
            description.getStyle().set("white-space", "pre-wrap");
            return description;
        }));
        setDetailsVisibleOnClick(true);

        setItems(workflowRepository.findByWorkflowStatusNot(WorkflowStatus.DONE));
    }
}
