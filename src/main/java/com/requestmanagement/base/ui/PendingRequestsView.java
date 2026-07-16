package com.requestmanagement.base.ui;

import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "pending-requests", layout = MainLayout.class)
@PageTitle("Gelen Talepler")
@RolesAllowed("PRODUCT_OWNER")
public class PendingRequestsView extends VerticalLayout {

    public PendingRequestsView(RequestRepository requestRepository, PrioritizationRepository prioritizationRepository,
                                WorkflowRepository workflowRepository) {
        PendingRequestsGrid grid = new PendingRequestsGrid(requestRepository, prioritizationRepository, workflowRepository);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Müşteri veya Başlık ara...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> grid.search(e.getValue()));

        setSizeFull();
        add(searchField, grid);
    }
}
