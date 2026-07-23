package com.requestmanagement.base.ui.customer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.CurrentUserResolver;
import com.requestmanagement.base.ui.shared.GridRowHighlighter;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/** The "Taleplerim" page: the new-request form on top of the customer's own request history. */
@Route(value = "create-request", layout = MainLayout.class)
@RolesAllowed("CUSTOMER")
public class CustomerRequestView extends VerticalLayout implements BeforeEnterObserver {

    private final transient RequestRepository requestRepository;
    private final transient UserRepository userRepository;
    private final RequestHistoryGrid historyGrid;

    public CustomerRequestView(RequestRepository requestRepository, UserRepository userRepository,
                                NotificationRepository notificationRepository,
                                RequestActivityRepository activityRepository,
                                RequestAttachmentRepository attachmentRepository,
                                RequestMessageRepository messageRepository, WorkflowRepository workflowRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        setWidthFull();

        RequestFormPanel formPanel = new RequestFormPanel(requestRepository, userRepository, notificationRepository,
                activityRepository, attachmentRepository, this::getCurrentCustomer, this::refreshHistory);
        historyGrid = new RequestHistoryGrid(requestRepository, activityRepository, attachmentRepository,
                messageRepository, notificationRepository, userRepository, workflowRepository,
                this::currentCustomerRequests, this::getCurrentCustomer);

        add(formPanel, new Hr(), historyGrid);
    }

    private void refreshHistory() {
        historyGrid.refresh();
    }

    private List<Request> currentCustomerRequests() {
        return requestRepository.findByCustomer_Email(getCurrentCustomer().getEmail());
    }

    private AppUser getCurrentCustomer() {
        return CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.CUSTOMER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getLocation().getQueryParameters().getSingleParameter("highlight").map(Long::valueOf)
                .ifPresent(id -> GridRowHighlighter.apply(historyGrid.grid, Request::getRequestId, id));
    }
}
