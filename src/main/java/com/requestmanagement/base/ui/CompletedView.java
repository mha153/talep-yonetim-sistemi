package com.requestmanagement.base.ui;

import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("archive")
@RolesAllowed({"PRODUCT_OWNER", "DEVELOPER"})
public class CompletedView extends VerticalLayout {

    public CompletedView(RequestRepository requestRepository, PrioritizationRepository prioritizationRepository,
                          WorkflowRepository workflowRepository) {
        setSizeFull();
        add(new ArchivedRequestsGrid(requestRepository, prioritizationRepository, workflowRepository));
    }
}
