package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
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

/** PO's read-only view of what's currently moving through the developer Sprint pipeline. */
@Route(value = "sprint-tracking", layout = MainLayout.class)
@PageTitle("Sprint Takibi")
@RolesAllowed("PRODUCT_OWNER")
public class SprintTrackingView extends VerticalLayout {

    public SprintTrackingView(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository,
                               UserRepository userRepository, NotificationRepository notificationRepository,
                               RequestActivityRepository activityRepository,
                               RequestMessageRepository messageRepository) {
        AppUser currentPo = CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.PRODUCT_OWNER);
        setSizeFull();
        add(new SprintTrackingGrid(workflowRepository, prioritizationRepository, activityRepository,
                messageRepository, notificationRepository, userRepository, currentPo));
    }
}
