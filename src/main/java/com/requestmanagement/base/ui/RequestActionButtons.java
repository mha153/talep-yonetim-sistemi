package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/** Builds the per-row "kebab" actions menu (prioritize / convert / reject) for {@link PendingRequestsGrid}. */
final class RequestActionButtons {

    private RequestActionButtons() {
    }

    static Component create(Request request, RequestRepository requestRepository,
                             PrioritizationRepository prioritizationRepository,
                             WorkflowRepository workflowRepository, Runnable onChange) {
        Button menuButton = new Button(new Icon(VaadinIcon.ELLIPSIS_DOTS_V));
        menuButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        ContextMenu contextMenu = new ContextMenu(menuButton);
        contextMenu.setOpenOnClick(true);

        contextMenu.addItem("Önceliklendir", e ->
                new PrioritizationDialog(request, prioritizationRepository, requestRepository, onChange).open());

        boolean hasWorkflow = workflowRepository.existsByRequest(request);
        if (RequestStatus.PRIORITIZED.equals(request.getStatus()) && !hasWorkflow) {
            contextMenu.addItem("Sprint'e Al", e -> {
                Workflow workflow = new Workflow();
                workflow.setRequest(request);
                workflowRepository.save(workflow);
                Toast.show("Talep iş akışına aktarıldı.");
                onChange.run();
            });
        }

        contextMenu.addItem("Reddet", e -> {
            request.setStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
            onChange.run();
        });

        return menuButton;
    }
}
