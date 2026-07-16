package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Comparator;
import java.util.List;

class PendingRequestsGrid extends Grid<Request> {

    private static final List<RequestStatus> LISTED_STATUSES =
            List.of(RequestStatus.NEW, RequestStatus.UNDER_REVIEW, RequestStatus.PRIORITIZED);

    private final RequestRepository requestRepository;
    private final PrioritizationRepository prioritizationRepository;
    private final WorkflowRepository workflowRepository;
    private String searchText = "";

    PendingRequestsGrid(RequestRepository requestRepository, PrioritizationRepository prioritizationRepository,
                         WorkflowRepository workflowRepository) {
        super(Request.class, false);
        this.requestRepository = requestRepository;
        this.prioritizationRepository = prioritizationRepository;
        this.workflowRepository = workflowRepository;

        setSizeFull();
        configureColumns();
        configureDetails();
        refresh();
    }

    void search(String text) {
        this.searchText = text == null ? "" : text.toLowerCase();
        refresh();
    }

    void refresh() {
        List<Request> requests = requestRepository.findByStatusIn(LISTED_STATUSES).stream()
                .filter(r -> !workflowRepository.existsByRequest(r))
                .toList();
        if (!searchText.isBlank()) {
            requests = requests.stream()
                    .filter(r -> r.getCustomer().getNameSurname().toLowerCase().contains(searchText)
                            || r.getTitle().toLowerCase().contains(searchText))
                    .toList();
        }
        requests = requests.stream()
                .sorted(Comparator.comparingInt(this::scoreOf).reversed())
                .toList();
        setItems(requests);
    }

    private void configureColumns() {
        addColumn(Request::getRequestId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        addColumn(request -> request.getCustomer().getNameSurname()).setHeader("Müşteri").setFlexGrow(1);
        addColumn(Request::getTitle).setHeader("Talep Başlığı").setFlexGrow(2);
        addColumn(new ComponentRenderer<>(request ->
                RequestScoreBadge.create(prioritizationRepository.findByRequest(request).orElse(null))))
                .setHeader("Skor").setWidth("130px").setFlexGrow(0);
        addColumn(request -> request.getStatus().displayLabel()).setHeader("Durum").setWidth("160px").setFlexGrow(0);
        addComponentColumn(request -> RequestActionButtons.create(
                request, requestRepository, prioritizationRepository, workflowRepository, this::refresh))
                .setHeader("İşlemler").setWidth("80px").setFlexGrow(0);
    }

    private void configureDetails() {
        setItemDetailsRenderer(new ComponentRenderer<>(request -> {
            Span description = new Span("Açıklama: " + request.getDescription());
            description.getStyle().set("white-space", "pre-wrap");
            return description;
        }));
        setDetailsVisibleOnClick(true);
    }

    private int scoreOf(Request request) {
        return prioritizationRepository.findByRequest(request)
                .map(p -> p.getPriorityScore())
                .orElse(-1);
    }
}
