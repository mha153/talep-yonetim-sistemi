package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Prioritization;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.BarChart;
import com.requestmanagement.base.ui.shared.PieChart;
import com.requestmanagement.base.ui.shared.RequestScoreBadge;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Builds the pie/bar chart data shown on {@link DashboardView}. */
final class DashboardCharts {

    private static final List<String> PRIORITY_LABEL_ORDER = List.of("Kritik", "Orta", "Düşük");

    private DashboardCharts() {
    }

    static List<PieChart.Slice> statusSlices(RequestRepository requestRepository,
                                              WorkflowRepository workflowRepository) {
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

    static List<BarChart.Bar> developerBars(WorkflowRepository workflowRepository, UserRepository userRepository) {
        List<Workflow> done = workflowRepository.findByWorkflowStatus(WorkflowStatus.DONE);
        Map<String, Long> countsByDeveloper = done.stream()
                .collect(Collectors.groupingBy(w -> w.getDeveloper().getNameSurname(), Collectors.counting()));
        return userRepository.findByRole(Role.DEVELOPER).stream()
                .map(AppUser::getNameSurname)
                .sorted()
                .map(name -> new BarChart.Bar(name, countsByDeveloper.getOrDefault(name, 0L), "#1e88e5"))
                .toList();
    }

    static List<BarChart.Bar> priorityBars(PrioritizationRepository prioritizationRepository) {
        List<Prioritization> all = prioritizationRepository.findAll();
        Map<String, Long> countsByLabel = all.stream()
                .collect(Collectors.groupingBy(p -> RequestScoreBadge.shortLabel(RequestScoreBadge.agedScore(p)),
                        Collectors.counting()));
        return PRIORITY_LABEL_ORDER.stream()
                .map(label -> new BarChart.Bar(label, countsByLabel.getOrDefault(label, 0L), colorFor(label)))
                .toList();
    }

    private static String colorFor(String label) {
        return switch (label) {
            case "Kritik" -> "#e53935";
            case "Orta" -> "#fdd835";
            default -> "#43a047";
        };
    }
}
