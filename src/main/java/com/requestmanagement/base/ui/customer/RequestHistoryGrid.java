package com.requestmanagement.base.ui.customer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.RequestDetailsPanel;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/** Shows a customer's own past requests, with cancel/message actions on each row. */
class RequestHistoryGrid extends VerticalLayout {

    private final transient WorkflowRepository workflowRepository;
    private final transient Supplier<List<Request>> requestsSupplier;
    final Grid<Request> grid = new Grid<>(Request.class, false);

    RequestHistoryGrid(RequestRepository requestRepository, RequestActivityRepository activityRepository,
                        RequestAttachmentRepository attachmentRepository, RequestMessageRepository messageRepository,
                        NotificationRepository notificationRepository, UserRepository userRepository,
                        WorkflowRepository workflowRepository, Supplier<List<Request>> requestsSupplier,
                        Supplier<AppUser> currentUser) {
        this.workflowRepository = workflowRepository;
        this.requestsSupplier = requestsSupplier;

        grid.addColumn(Request::getTitle).setHeader("Talep Başlığı").setFlexGrow(1);
        grid.addColumn(this::statusLabel).setHeader("Son Durum").setWidth("180px").setFlexGrow(0);
        grid.addComponentColumn(request -> RequestHistoryRowActions.build(request, requestRepository,
                activityRepository, attachmentRepository, messageRepository, notificationRepository, userRepository,
                currentUser.get(), this::refresh)).setHeader("İşlem").setWidth("200px").setFlexGrow(0);
        grid.setItemDetailsRenderer(new ComponentRenderer<>(
                request -> new RequestDetailsPanel(request, activityRepository, attachmentRepository)));
        grid.setDetailsVisibleOnClick(true);

        setWidthFull();
        setPadding(false);
        add(new H3("Geçmiş Taleplerim / Takip"), grid);
        refresh();
    }

    private String statusLabel(Request request) {
        return workflowRepository.findByRequest(request)
                .map(w -> w.getWorkflowStatus().displayLabel())
                .orElse(request.getStatus().displayLabel());
    }

    void refresh() {
        grid.setItems(requestsSupplier.get().stream()
                .sorted(Comparator.comparing(Request::getRequestId).reversed())
                .toList());
    }
}
