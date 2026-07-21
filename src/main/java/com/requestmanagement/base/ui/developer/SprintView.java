package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.CurrentUserResolver;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.requestmanagement.base.ui.shared.RequestDetailsPanel;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

/** Shared Sprint pool: workflow items no developer has claimed yet. */
@Route(value = "sprint", layout = MainLayout.class)
@PageTitle("Sprint Havuzu")
@RolesAllowed("DEVELOPER")
public class SprintView extends VerticalLayout {

    public SprintView(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository,
                       UserRepository userRepository, NotificationRepository notificationRepository,
                       RequestActivityRepository activityRepository, RequestAttachmentRepository attachmentRepository,
                       RequestMessageRepository messageRepository) {
        AppUser currentDeveloper = CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.DEVELOPER);

        Grid<Workflow> grid = new Grid<>(Workflow.class, false);
        grid.addColumn(workflow -> workflow.getRequest().getRequestId())
                .setHeader("ID").setWidth("60px").setFlexGrow(0);
        grid.addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(1);
        grid.addColumn(workflow -> prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> String.valueOf(p.getPriorityScore()))
                .orElse("Belirlenmedi")).setHeader("Skor").setWidth("100px").setFlexGrow(0);

        grid.addComponentColumn(workflow -> SprintPoolRowActions.build(workflow, workflowRepository,
                prioritizationRepository, userRepository, notificationRepository, activityRepository,
                messageRepository, currentDeveloper, grid))
                .setHeader("İşlem");

        grid.setItemDetailsRenderer(new ComponentRenderer<>(
                workflow -> new RequestDetailsPanel(workflow.getRequest(), activityRepository, attachmentRepository)));
        grid.setDetailsVisibleOnClick(true);

        grid.setItems(SprintPoolSorter.byScoreDescending(
                workflowRepository.findByDeveloperIsNullAndWorkflowStatusNot(WorkflowStatus.DONE),
                prioritizationRepository));
        grid.setSizeFull();

        setSizeFull();
        add(grid);
    }
}
