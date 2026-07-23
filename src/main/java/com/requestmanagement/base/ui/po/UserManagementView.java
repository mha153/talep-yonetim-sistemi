package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.EmailChangeRequestRepository;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RegistrationRequestRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.CurrentUserResolver;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

/** The "Kullanıcılar" page: lets the PO change roles, remove accounts, or approve pending sign-up/e-posta requests. */
@Route(value = "users", layout = MainLayout.class)
@PageTitle("Kullanıcılar")
@RolesAllowed("PRODUCT_OWNER")
public class UserManagementView extends VerticalLayout {

    public UserManagementView(UserRepository userRepository, RequestRepository requestRepository,
                               WorkflowRepository workflowRepository, NotificationRepository notificationRepository,
                               RequestMessageRepository messageRepository,
                               RegistrationRequestRepository registrationRequestRepository,
                               EmailChangeRequestRepository emailChangeRequestRepository) {
        AppUser currentPo = CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.PRODUCT_OWNER);
        setSizeFull();
        add(new UserManagementGrid(userRepository, requestRepository, workflowRepository, notificationRepository,
                        messageRepository, emailChangeRequestRepository, currentPo),
                new H4("Bekleyen Talepler"),
                new PendingAccountRequestsGrid(registrationRequestRepository, emailChangeRequestRepository,
                        userRepository, notificationRepository, currentPo));
    }
}
