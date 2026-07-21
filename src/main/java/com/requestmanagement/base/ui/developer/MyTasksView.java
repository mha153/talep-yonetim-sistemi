package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.CurrentUserResolver;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

/** A developer's own claimed tasks, separate from the shared Sprint pool. */
@Route(value = "my-tasks", layout = MainLayout.class)
@PageTitle("Görevlerim")
@RolesAllowed("DEVELOPER")
public class MyTasksView extends VerticalLayout {

    public MyTasksView(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository,
                        UserRepository userRepository, NotificationRepository notificationRepository,
                        RequestActivityRepository activityRepository,
                        RequestAttachmentRepository attachmentRepository,
                        RequestMessageRepository messageRepository) {
        AppUser currentDeveloper = CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.DEVELOPER);
        setSizeFull();
        add(new MyTasksGrid(workflowRepository, prioritizationRepository, activityRepository, attachmentRepository,
                messageRepository, notificationRepository, userRepository, currentDeveloper));
    }
}
