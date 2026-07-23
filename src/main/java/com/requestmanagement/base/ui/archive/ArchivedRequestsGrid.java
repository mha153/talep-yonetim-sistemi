package com.requestmanagement.base.ui.archive;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.RequestDetailsPanel;
import com.requestmanagement.base.ui.shared.RequestScoreBadge;
import com.requestmanagement.base.ui.shared.RequestSearchFilter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Read-only table of finished requests: rejected ones, plus completed workflow items. */
class ArchivedRequestsGrid extends Grid<Request> {

    private final transient RequestRepository requestRepository;
    private final transient WorkflowRepository workflowRepository;
    private String searchText = "";

    ArchivedRequestsGrid(RequestRepository requestRepository, PrioritizationRepository prioritizationRepository,
                          WorkflowRepository workflowRepository, RequestActivityRepository activityRepository,
                          RequestAttachmentRepository attachmentRepository) {
        super(Request.class, false);
        this.requestRepository = requestRepository;
        this.workflowRepository = workflowRepository;

        setSizeFull();
        addColumn(Request::getRequestId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        addColumn(request -> request.getCustomer().getNameSurname()).setHeader("Müşteri").setFlexGrow(1);
        addColumn(Request::getTitle).setHeader("Başlık").setFlexGrow(2);
        addColumn(new ComponentRenderer<>(request ->
                RequestScoreBadge.create(prioritizationRepository.findByRequest(request).orElse(null))))
                .setHeader("Skor").setWidth("150px").setFlexGrow(0);
        addColumn(this::outcomeLabel).setHeader("Sonuç").setWidth("160px").setFlexGrow(0);

        setItemDetailsRenderer(new ComponentRenderer<>(
                request -> new RequestDetailsPanel(request, activityRepository, attachmentRepository)));
        setDetailsVisibleOnClick(true);

        refresh();
    }

    void search(String text) {
        this.searchText = text;
        refresh();
    }

    void refresh() {
        List<Request> rejected = requestRepository.findByStatus(RequestStatus.REJECTED);
        List<Request> completed = workflowRepository.findByWorkflowStatus(WorkflowStatus.DONE).stream()
                .map(Workflow::getRequest)
                .toList();
        List<Request> requests = Stream.concat(rejected.stream(), completed.stream())
                .sorted(Comparator.comparing(Request::getRequestId).reversed())
                .toList();
        setItems(RequestSearchFilter.apply(requests, searchText,
                r -> r.getCustomer().getNameSurname(), Request::getTitle));
    }

    private String outcomeLabel(Request request) {
        if (RequestStatus.REJECTED.equals(request.getStatus())) {
            return "Reddedildi";
        }
        return workflowRepository.findByRequest(request)
                .map(workflow -> workflow.getWorkflowStatus().displayLabel())
                .orElse(request.getStatus().displayLabel());
    }
}
