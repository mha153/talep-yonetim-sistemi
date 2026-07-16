package com.requestmanagement.base.ui;

import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/** PO's read-only view of what's currently moving through the developer Sprint pipeline. */
@Route(value = "sprint-tracking", layout = MainLayout.class)
@PageTitle("Sprint Takibi")
@RolesAllowed("PRODUCT_OWNER")
public class SprintTrackingView extends VerticalLayout {

    public SprintTrackingView(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository) {
        setSizeFull();
        add(new SprintTrackingGrid(workflowRepository, prioritizationRepository));
    }
}
