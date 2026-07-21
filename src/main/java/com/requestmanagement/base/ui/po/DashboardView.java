package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/** PO-facing overview: every request's lifecycle stage shown as a single pie chart. */
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Genel Bakış")
@RolesAllowed("PRODUCT_OWNER")
public class DashboardView extends VerticalLayout {

    private final transient RequestRepository requestRepository;
    private final transient WorkflowRepository workflowRepository;
    private final VerticalLayout chartContainer = new VerticalLayout();

    public DashboardView(RequestRepository requestRepository, WorkflowRepository workflowRepository) {
        this.requestRepository = requestRepository;
        this.workflowRepository = workflowRepository;

        Button refreshButton = new Button("Yenile", new Icon(VaadinIcon.REFRESH), e -> renderChart());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        chartContainer.setSizeFull();
        chartContainer.setPadding(false);

        setSizeFull();
        setPadding(true);
        add(refreshButton, chartContainer);
        setFlexGrow(1, chartContainer);
        renderChart();
    }

    private void renderChart() {
        chartContainer.removeAll();
        chartContainer.add(PieChart.create(buildSlices()));
    }

    private List<PieChart.Slice> buildSlices() {
        long newCount = requestRepository
                .findByStatusIn(List.of(RequestStatus.NEW, RequestStatus.UNDER_REVIEW)).size();
        long prioritizedOnlyCount = requestRepository.findByStatus(RequestStatus.PRIORITIZED).stream()
                .filter(r -> !workflowRepository.existsByRequest(r))
                .count();
        long rejectedCount = requestRepository.findByStatus(RequestStatus.REJECTED).size();
        long sprintPoolCount = workflowRepository.findByWorkflowStatus(WorkflowStatus.BACKLOG).size();
        long inProgressCount = workflowRepository.findByWorkflowStatus(WorkflowStatus.IN_PROGRESS).size()
                + workflowRepository.findByWorkflowStatus(WorkflowStatus.TESTING).size();
        long doneCount = workflowRepository.findByWorkflowStatus(WorkflowStatus.DONE).size();

        return List.of(
                new PieChart.Slice("Yeni", newCount, "#9e9e9e"),
                new PieChart.Slice("Önceliklendirilmiş", prioritizedOnlyCount, "#fdd835"),
                new PieChart.Slice("Reddedilmiş", rejectedCount, "#e53935"),
                new PieChart.Slice("Sprint Havuzunda", sprintPoolCount, "#8e24aa"),
                new PieChart.Slice("Üstlenilmiş", inProgressCount, "#1e88e5"),
                new PieChart.Slice("Tamamlanmış", doneCount, "#43a047"));
    }
}
