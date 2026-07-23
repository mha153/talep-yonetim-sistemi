package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Prioritization;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.BarChart;
import com.requestmanagement.base.ui.shared.PieChart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Builds the status pie chart and effort distribution bar chart shown on {@link DeveloperAnalyticsView}. */
final class DeveloperAnalyticsData {

    private static final Map<Integer, String> EFFORT_LABELS = new LinkedHashMap<>();

    static {
        EFFORT_LABELS.put(1, "1 - Çok Kolay");
        EFFORT_LABELS.put(2, "2 - Kolay");
        EFFORT_LABELS.put(3, "3 - Orta");
        EFFORT_LABELS.put(4, "4 - Zor");
        EFFORT_LABELS.put(5, "5 - Çok Zor");
    }

    private static final String LABEL_UNSET = "Belirtilmemiş";

    private DeveloperAnalyticsData() {
    }

    static List<PieChart.Slice> statusSlices(AppUser developer, WorkflowRepository workflowRepository) {
        List<Workflow> workflows = workflowRepository.findByDeveloper(developer);
        Map<WorkflowStatus, Long> countsByStatus = workflows.stream()
                .collect(Collectors.groupingBy(Workflow::getWorkflowStatus, Collectors.counting()));

        return List.of(
                slice(WorkflowStatus.BACKLOG, countsByStatus, "#8e24aa"),
                slice(WorkflowStatus.IN_PROGRESS, countsByStatus, "#1e88e5"),
                slice(WorkflowStatus.TESTING, countsByStatus, "#fb8c00"),
                slice(WorkflowStatus.DONE, countsByStatus, "#43a047"));
    }

    private static PieChart.Slice slice(WorkflowStatus status, Map<WorkflowStatus, Long> countsByStatus,
                                         String color) {
        return new PieChart.Slice(status.displayLabel(), countsByStatus.getOrDefault(status, 0L), color);
    }

    static List<BarChart.Bar> effortBars(AppUser developer, WorkflowRepository workflowRepository,
                                          PrioritizationRepository prioritizationRepository) {
        List<Workflow> workflows = workflowRepository.findByDeveloper(developer);
        Map<String, Long> countsByLabel = workflows.stream()
                .map(w -> prioritizationRepository.findByRequest(w.getRequest()).map(Prioritization::getEffort)
                        .map(EFFORT_LABELS::get).orElse(LABEL_UNSET))
                .collect(Collectors.groupingBy(label -> label, Collectors.counting()));

        List<BarChart.Bar> bars = EFFORT_LABELS.values().stream()
                .map(label -> new BarChart.Bar(label, countsByLabel.getOrDefault(label, 0L), "#1e88e5"))
                .collect(Collectors.toCollection(ArrayList::new));
        bars.add(new BarChart.Bar(LABEL_UNSET, countsByLabel.getOrDefault(LABEL_UNSET, 0L), "#9e9e9e"));
        return bars;
    }
}
