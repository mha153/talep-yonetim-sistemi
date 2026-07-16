package com.talep.base.ui;

import com.talep.base.model.Workflow;
import com.talep.base.model.WorkflowStatus;
import com.talep.base.repository.PrioritizationRepository;
import com.talep.base.repository.WorkflowRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("sprint")
@RolesAllowed("DEVELOPER")
public class SprintView extends VerticalLayout {

    public SprintView(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository) {
        Grid<Workflow> grid = new Grid<>(Workflow.class, false);
        grid.addColumn(Workflow::getTaskId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        grid.addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(1);
        grid.addColumn(workflow -> workflow.getRequest().getDescription()).setHeader("Açıklama").setFlexGrow(2);
        grid.addColumn(workflow -> prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> String.valueOf(p.getPriorityScore()))
                .orElse("Belirlenmedi")).setHeader("Skor").setWidth("100px").setFlexGrow(0);

        grid.addComponentColumn(workflow -> {
            Button btnTamamla = new Button("Tamamla", e -> {
                workflow.setWorkflowStatus(WorkflowStatus.DONE);
                workflowRepository.save(workflow);
                Notification.show("İş başarıyla tamamlandı!");
                grid.setItems(workflowRepository.findByWorkflowStatusNot(WorkflowStatus.DONE));
            });
            btnTamamla.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            return btnTamamla;
        }).setHeader("İşlem");

        grid.setItems(workflowRepository.findByWorkflowStatusNot(WorkflowStatus.DONE));
        grid.setSizeFull();

        setSizeFull();
        add(grid);
    }
}
