package com.requestmanagement.base.ui.customer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.messaging.MessageIndicatorIcon;
import com.requestmanagement.base.ui.messaging.RequestConversationDialog;
import com.requestmanagement.base.ui.shared.ActivityRecorder;
import com.requestmanagement.base.ui.shared.RequestDetailsPanel;
import com.requestmanagement.base.ui.shared.Toast;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;
import java.util.function.Supplier;

/** Shows a customer's own past requests, with cancel/message actions on each row. */
class RequestHistoryGrid extends VerticalLayout {

    private final transient RequestRepository requestRepository;
    private final transient RequestActivityRepository activityRepository;
    private final transient RequestMessageRepository messageRepository;
    private final transient NotificationRepository notificationRepository;
    private final transient UserRepository userRepository;
    private final transient WorkflowRepository workflowRepository;
    private final transient Supplier<List<Request>> requestsSupplier;
    private final transient Supplier<AppUser> currentUser;
    final Grid<Request> grid = new Grid<>(Request.class, false);

    RequestHistoryGrid(RequestRepository requestRepository, RequestActivityRepository activityRepository,
                        RequestAttachmentRepository attachmentRepository, RequestMessageRepository messageRepository,
                        NotificationRepository notificationRepository, UserRepository userRepository,
                        WorkflowRepository workflowRepository, Supplier<List<Request>> requestsSupplier, Supplier<AppUser> currentUser) {
        this.requestRepository = requestRepository;
        this.activityRepository = activityRepository;
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.workflowRepository = workflowRepository;
        this.requestsSupplier = requestsSupplier;
        this.currentUser = currentUser;

        grid.addColumn(Request::getTitle).setHeader("Talep Başlığı").setFlexGrow(1);
        grid.addColumn(this::statusLabel).setHeader("Son Durum").setWidth("180px").setFlexGrow(0);
        grid.addComponentColumn(this::buildActions).setHeader("İşlem").setWidth("200px").setFlexGrow(0);
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

    private HorizontalLayout buildActions(Request request) {
        Button messageButton = new Button("Mesajlaş", e -> new RequestConversationDialog(
                request, MessageChannel.CUSTOMER_PO, "Müşteri Görüşmesi", messageRepository, notificationRepository,
                userRepository, currentUser.get(), this::refresh).open());
        messageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        boolean hasUnread = messageRepository.existsByRequestAndChannelAndReadFalseAndAuthorNot(
                request, MessageChannel.CUSTOMER_PO, currentUser.get());

        if (!RequestStatus.NEW.equals(request.getStatus())) {
            return new HorizontalLayout(MessageIndicatorIcon.wrap(messageButton, hasUnread));
        }
        Button cancelButton = new Button("İptal Et", e -> {
            request.setStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
            ActivityRecorder.record(activityRepository, request, "Müşteri tarafından iptal edildi");
            refresh();
            Toast.show("Talep iptal edildi.");
        });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        return new HorizontalLayout(MessageIndicatorIcon.wrap(messageButton, hasUnread), cancelButton);
    }

    void refresh() {
        grid.setItems(requestsSupplier.get());
    }
}
