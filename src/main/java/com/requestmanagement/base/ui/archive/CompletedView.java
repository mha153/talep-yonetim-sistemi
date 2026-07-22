package com.requestmanagement.base.ui.archive;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.GridRowHighlighter;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("archive")
@RolesAllowed({"PRODUCT_OWNER", "DEVELOPER"})
public class CompletedView extends VerticalLayout implements BeforeEnterObserver {

    private final ArchivedRequestsGrid grid;

    public CompletedView(RequestRepository requestRepository, PrioritizationRepository prioritizationRepository,
                          WorkflowRepository workflowRepository, RequestActivityRepository activityRepository,
                          RequestAttachmentRepository attachmentRepository) {
        grid = new ArchivedRequestsGrid(requestRepository, prioritizationRepository, workflowRepository,
                activityRepository, attachmentRepository);

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
