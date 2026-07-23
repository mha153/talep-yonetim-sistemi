package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.BarChart;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.requestmanagement.base.ui.shared.PieChart;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/** PO-facing overview: request lifecycle pie chart, plus per-developer and per-priority bar charts. */
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Genel Bakış")
@RolesAllowed("PRODUCT_OWNER")
public class DashboardView extends VerticalLayout {

    private final transient RequestRepository requestRepository;
    private final transient WorkflowRepository workflowRepository;
    private final transient PrioritizationRepository prioritizationRepository;
    private final transient UserRepository userRepository;
    private final VerticalLayout chartContainer = new VerticalLayout();

    public DashboardView(RequestRepository requestRepository, WorkflowRepository workflowRepository,
                          PrioritizationRepository prioritizationRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.workflowRepository = workflowRepository;
        this.prioritizationRepository = prioritizationRepository;
        this.userRepository = userRepository;

        Button refreshButton = new Button("Yenile", new Icon(VaadinIcon.REFRESH), e -> renderCharts());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        chartContainer.setPadding(false);

        setSizeFull();
        setPadding(true);
        add(refreshButton, chartContainer);
        renderCharts();
    }

    private void renderCharts() {
        chartContainer.removeAll();
        VerticalLayout pieSection = new VerticalLayout(
                PieChart.create(DashboardCharts.statusSlices(requestRepository, workflowRepository)));
        pieSection.setPadding(false);
        pieSection.setHeightFull();

        chartContainer.add(pieSection,
                new H4("Developer Başına Tamamlanan İş"),
                BarChart.create(DashboardCharts.developerBars(workflowRepository, userRepository)),
                new H4("Öncelik Dağılımı"),
                BarChart.create(DashboardCharts.priorityBars(prioritizationRepository)));
    }
}
