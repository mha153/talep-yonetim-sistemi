package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("sprint")
@RolesAllowed("DEVELOPER")
public class SprintView extends VerticalLayout {

    public SprintView(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository,
                       UserRepository userRepository) {
        Grid<Workflow> grid = new Grid<>(Workflow.class, false);
        grid.addColumn(Workflow::getTaskId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        grid.addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(1);
        grid.addColumn(workflow -> prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> String.valueOf(p.getPriorityScore()))
                .orElse("Belirlenmedi")).setHeader("Skor").setWidth("100px").setFlexGrow(0);
        grid.addColumn(workflow -> workflow.getWorkflowStatus().displayLabel())
                .setHeader("Durum").setWidth("140px").setFlexGrow(0);
        grid.addColumn(this::assignedDeveloperLabel).setHeader("Üstlenen").setWidth("140px").setFlexGrow(0);

        grid.addComponentColumn(workflow -> buildActionButton(workflow, workflowRepository, userRepository, grid))
                .setHeader("İşlem");

        grid.setItemDetailsRenderer(new ComponentRenderer<>(workflow -> {
            Span description = new Span("Açıklama: " + workflow.getRequest().getDescription());
            description.getStyle().set("white-space", "pre-wrap");
            return description;
        }));
        grid.setDetailsVisibleOnClick(true);

        grid.setItems(workflowRepository.findByWorkflowStatusNot(WorkflowStatus.DONE));
        grid.setSizeFull();

        setSizeFull();
        add(grid);
    }

    private String assignedDeveloperLabel(Workflow workflow) {
        return workflow.getDeveloper() == null ? "Atanmadı" : workflow.getDeveloper().getNameSurname();
    }

    private Button buildActionButton(Workflow workflow, WorkflowRepository workflowRepository,
                                      UserRepository userRepository, Grid<Workflow> grid) {
        if (workflow.getDeveloper() == null) {
            Button claimButton = new Button("Üstlen", e -> {
                AppUser developer = CurrentUserResolver.findOrCreate(
                        userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.DEVELOPER);
                workflow.setDeveloper(developer);
                workflow.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);
                workflowRepository.save(workflow);
                Toast.show("Görevi üstlendiniz.");
                grid.setItems(workflowRepository.findByWorkflowStatusNot(WorkflowStatus.DONE));
            });
            claimButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return claimButton;
        }

        Button completeButton = new Button("Tamamla", e -> {
            workflow.setWorkflowStatus(WorkflowStatus.DONE);
            workflowRepository.save(workflow);
            Toast.show("İş başarıyla tamamlandı!");
            grid.setItems(workflowRepository.findByWorkflowStatusNot(WorkflowStatus.DONE));
        });
        completeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        return completeButton;
    }
}
