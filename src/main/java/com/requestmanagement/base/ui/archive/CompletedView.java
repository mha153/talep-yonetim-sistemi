package com.requestmanagement.base.ui.archive;

import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("archive")
@RolesAllowed({"PRODUCT_OWNER", "DEVELOPER"})
public class CompletedView extends VerticalLayout {

    public CompletedView(RequestRepository requestRepository, PrioritizationRepository prioritizationRepository,
                          WorkflowRepository workflowRepository, RequestActivityRepository activityRepository,
                          RequestAttachmentRepository attachmentRepository) {
        setSizeFull();
        add(new ArchivedRequestsGrid(requestRepository, prioritizationRepository, workflowRepository,
                activityRepository, attachmentRepository));
    }
}
