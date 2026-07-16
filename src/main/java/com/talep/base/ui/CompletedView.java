package com.talep.base.ui;

import com.talep.base.model.Workflow;
import com.talep.base.model.WorkflowStatus;
import com.talep.base.repository.PrioritizationRepository;
import com.talep.base.repository.WorkflowRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("arsiv")
@RolesAllowed({"PRODUCT_OWNER", "DEVELOPER"})
public class CompletedView extends VerticalLayout {

    public CompletedView(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository) {
        Grid<Workflow> grid = new Grid<>(Workflow.class, false);
        grid.addColumn(Workflow::getTaskId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        grid.addColumn(workflow -> workflow.getRequest().getCustomer().getNameSurname()).setHeader("Müşteri").setFlexGrow(1);
        grid.addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(1);
        grid.addColumn(workflow -> workflow.getRequest().getDescription()).setHeader("Açıklama").setFlexGrow(2);
        grid.addColumn(workflow -> prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> String.valueOf(p.getPriorityScore()))
                .orElse("Belirlenmedi")).setHeader("Skor").setWidth("100px").setFlexGrow(0);

        grid.setItems(workflowRepository.findByWorkflowStatus(WorkflowStatus.DONE));
        grid.setSizeFull();

        setSizeFull();
        add(grid);
    }
}
