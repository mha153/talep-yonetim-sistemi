package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.RequestDetailsPanel;
import com.requestmanagement.base.ui.shared.RequestScoreBadge;
import com.requestmanagement.base.ui.shared.RequestSearchFilter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Comparator;
import java.util.List;

/** Table of requests awaiting PO triage (new, under review, or prioritized but not yet in a workflow). */
class PendingRequestsGrid extends Grid<Request> {

    private static final List<RequestStatus> LISTED_STATUSES =
            List.of(RequestStatus.NEW, RequestStatus.UNDER_REVIEW, RequestStatus.PRIORITIZED);

    private final transient RequestRepository requestRepository;
    private final transient PrioritizationRepository prioritizationRepository;
    private final transient WorkflowRepository workflowRepository;
    private final transient RequestActivityRepository activityRepository;
    private final transient RequestAttachmentRepository attachmentRepository;
    private String searchText = "";

    PendingRequestsGrid(RequestRepository requestRepository, PrioritizationRepository prioritizationRepository,
                         WorkflowRepository workflowRepository, UserRepository userRepository,
                         NotificationRepository notificationRepository, RequestActivityRepository activityRepository,
                         RequestAttachmentRepository attachmentRepository, RequestMessageRepository messageRepository,
                         AppUser currentPo) {
        super(Request.class, false);
        this.requestRepository = requestRepository;
        this.prioritizationRepository = prioritizationRepository;
        this.workflowRepository = workflowRepository;
        this.activityRepository = activityRepository;
        this.attachmentRepository = attachmentRepository;

        setSizeFull();
        configureColumns(userRepository, notificationRepository, messageRepository, currentPo);
        configureDetails();
        refresh();
    }

    void search(String text) {
        this.searchText = text;
        refresh();
    }

    void refresh() {
        List<Request> requests = requestRepository.findByStatusIn(LISTED_STATUSES).stream()
                .filter(r -> !workflowRepository.existsByRequest(r))
                .toList();
        requests = RequestSearchFilter.apply(requests, searchText,
                r -> r.getCustomer().getNameSurname(), Request::getTitle);
        Comparator<Request> byScoreThenNewest = Comparator.comparingInt(this::scoreOf).reversed()
                .thenComparing(Comparator.comparing(Request::getRequestId).reversed());
        setItems(requests.stream().sorted(byScoreThenNewest).toList());
    }

    private void configureColumns(UserRepository userRepository, NotificationRepository notificationRepository,
                                   RequestMessageRepository messageRepository, AppUser currentPo) {
        addColumn(Request::getRequestId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        addColumn(request -> request.getCustomer().getNameSurname()).setHeader("Müşteri").setFlexGrow(1);
        addColumn(Request::getTitle).setHeader("Talep Başlığı").setFlexGrow(2);
        addColumn(new ComponentRenderer<>(request ->
                RequestScoreBadge.create(prioritizationRepository.findByRequest(request).orElse(null))))
                .setHeader("Skor").setWidth("150px").setFlexGrow(0);
        addColumn(request -> request.getStatus().displayLabel()).setHeader("Durum").setWidth("160px").setFlexGrow(0);
        addComponentColumn(request -> RequestActionButtons.create(request, requestRepository,
                prioritizationRepository, workflowRepository, userRepository, notificationRepository,
                activityRepository, messageRepository, currentPo, this::refresh))
                .setHeader("İşlemler").setWidth("80px").setFlexGrow(0);
    }

    private void configureDetails() {
        setItemDetailsRenderer(new ComponentRenderer<>(
                request -> new RequestDetailsPanel(request, activityRepository, attachmentRepository)));
        setDetailsVisibleOnClick(true);
    }

    private int scoreOf(Request request) {
        return prioritizationRepository.findByRequest(request)
                .map(p -> p.getPriorityScore())
                .orElse(-1);
    }
}
