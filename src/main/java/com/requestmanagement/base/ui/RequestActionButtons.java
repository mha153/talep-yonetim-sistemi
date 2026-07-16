package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/** Builds the per-row action buttons (prioritize / convert / reject) for {@link PendingRequestsGrid}. */
final class RequestActionButtons {

    private RequestActionButtons() {
    }

    static HorizontalLayout create(Request request, RequestRepository requestRepository,
                                    PrioritizationRepository prioritizationRepository,
                                    WorkflowRepository workflowRepository, Runnable onChange) {
        Button prioritizeButton = new Button("Önceliklendir",
                e -> new PrioritizationDialog(request, prioritizationRepository, requestRepository, onChange).open());
        prioritizeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);

        boolean hasWorkflow = workflowRepository.existsByRequest(request);
        Button convertToWorkflowButton = new Button("Sprint'e Al", e -> {
            Workflow workflow = new Workflow();
            workflow.setRequest(request);
            workflowRepository.save(workflow);
            Notification.show("Talep iş akışına aktarıldı.");
            onChange.run();
        });
        convertToWorkflowButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        convertToWorkflowButton.setVisible(RequestStatus.PRIORITIZED.equals(request.getStatus()) && !hasWorkflow);

        Button rejectButton = new Button("Reddet", e -> {
            request.setStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
            onChange.run();
        });
        rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

        return new HorizontalLayout(prioritizeButton, convertToWorkflowButton, rejectButton);
    }
}
