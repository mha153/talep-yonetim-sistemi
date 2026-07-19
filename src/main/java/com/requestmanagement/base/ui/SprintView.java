package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
                       RequestActivityRepository activityRepository, RequestMessageRepository messageRepository) {
        AppUser currentDeveloper = CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.DEVELOPER);

        Grid<Workflow> grid = new Grid<>(Workflow.class, false);
        grid.addColumn(Workflow::getTaskId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        grid.addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(1);
        grid.addColumn(workflow -> prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> String.valueOf(p.getPriorityScore()))
                .orElse("Belirlenmedi")).setHeader("Skor").setWidth("100px").setFlexGrow(0);

        grid.addComponentColumn(workflow -> buildActions(workflow, workflowRepository, userRepository,
                notificationRepository, activityRepository, messageRepository, currentDeveloper, grid))
                .setHeader("İşlem");

        grid.setItemDetailsRenderer(new ComponentRenderer<>(
                workflow -> new RequestDetailsPanel(workflow.getRequest(), activityRepository)));
        grid.setDetailsVisibleOnClick(true);

        grid.setItems(workflowRepository.findByDeveloperIsNullAndWorkflowStatusNot(WorkflowStatus.DONE));
        grid.setSizeFull();

        setSizeFull();
        add(grid);
    }

    private HorizontalLayout buildActions(Workflow workflow, WorkflowRepository workflowRepository,
                                           UserRepository userRepository,
                                           NotificationRepository notificationRepository,
                                           RequestActivityRepository activityRepository,
                                           RequestMessageRepository messageRepository,
                                           AppUser currentDeveloper, Grid<Workflow> grid) {
        Button noteButton = new Button("Not", e -> new RequestConversationDialog(workflow.getRequest(),
                MessageChannel.INTERNAL, "Developer Notu", messageRepository, notificationRepository,
                userRepository, currentDeveloper, () -> grid.getDataProvider().refreshItem(workflow)).open());
        noteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        boolean unread = messageRepository.existsByRequestAndChannelAndReadFalseAndAuthorNot(
                workflow.getRequest(), MessageChannel.INTERNAL, currentDeveloper);

        Button claimButton = new Button("Üstlen", e -> {
            WorkflowActions.claim(workflow, currentDeveloper, workflowRepository, userRepository,
                    notificationRepository, activityRepository);
            Toast.show("Görevi üstlendiniz.");
            grid.setItems(workflowRepository.findByDeveloperIsNullAndWorkflowStatusNot(WorkflowStatus.DONE));
        });
        claimButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        return new HorizontalLayout(claimButton, MessageIndicatorIcon.wrap(noteButton, unread));
    }
}
