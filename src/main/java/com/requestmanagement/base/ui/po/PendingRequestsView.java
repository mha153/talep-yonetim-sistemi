package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.CurrentUserResolver;
import com.requestmanagement.base.ui.shared.GridRowHighlighter;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "pending-requests", layout = MainLayout.class)
@PageTitle("Gelen Talepler")
@RolesAllowed("PRODUCT_OWNER")
public class PendingRequestsView extends VerticalLayout implements BeforeEnterObserver {

    private final PendingRequestsGrid grid;

    public PendingRequestsView(RequestRepository requestRepository, PrioritizationRepository prioritizationRepository,
                                WorkflowRepository workflowRepository, UserRepository userRepository,
                                NotificationRepository notificationRepository,
                                RequestActivityRepository activityRepository,
                                RequestAttachmentRepository attachmentRepository,
                                RequestMessageRepository messageRepository) {
        AppUser currentPo = CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.PRODUCT_OWNER);
        grid = new PendingRequestsGrid(requestRepository, prioritizationRepository,
                workflowRepository, userRepository, notificationRepository, activityRepository, attachmentRepository,
                messageRepository, currentPo);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Müşteri veya Başlık ara...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> grid.search(e.getValue()));

        setSizeFull();
        add(searchField, grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getLocation().getQueryParameters().getSingleParameter("highlight").map(Long::valueOf)
                .ifPresent(id -> GridRowHighlighter.apply(grid, Request::getRequestId, id));
    }
}
