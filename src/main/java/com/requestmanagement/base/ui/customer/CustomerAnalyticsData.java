package com.requestmanagement.base.ui.customer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestActivity;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.PieChart;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/** Builds the status pie chart and average resolution time stat shown on {@link CustomerAnalyticsView}. */
final class CustomerAnalyticsData {

    private static final String LABEL_NEW = "Yeni";
    private static final String LABEL_PRIORITIZED = "Önceliklendirilmiş";
    private static final String LABEL_REJECTED = "Reddedilmiş";
    private static final String LABEL_BACKLOG = "Sprint Havuzunda";
    private static final String LABEL_IN_PROGRESS = "Üstlenilmiş";
    private static final String LABEL_DONE = "Tamamlanmış";

    private static final Map<String, String> STATUS_COLORS = new LinkedHashMap<>();

    static {
        STATUS_COLORS.put(LABEL_NEW, "#9e9e9e");
        STATUS_COLORS.put(LABEL_PRIORITIZED, "#fdd835");
        STATUS_COLORS.put(LABEL_REJECTED, "#e53935");
        STATUS_COLORS.put(LABEL_BACKLOG, "#8e24aa");
        STATUS_COLORS.put(LABEL_IN_PROGRESS, "#1e88e5");
        STATUS_COLORS.put(LABEL_DONE, "#43a047");
    }

    private CustomerAnalyticsData() {
    }

    static List<PieChart.Slice> statusSlices(AppUser customer, RequestRepository requestRepository,
                                              WorkflowRepository workflowRepository) {
        List<Request> requests = requestRepository.findByCustomer_Email(customer.getEmail());
        Map<String, Long> countsByLabel = requests.stream()
                .collect(Collectors.groupingBy(r -> bucketLabel(r, workflowRepository), Collectors.counting()));

        return STATUS_COLORS.entrySet().stream()
                .map(entry -> new PieChart.Slice(entry.getKey(), countsByLabel.getOrDefault(entry.getKey(), 0L),
                        entry.getValue()))
                .toList();
    }

    private static String bucketLabel(Request request, WorkflowRepository workflowRepository) {
        if (request.getStatus() == RequestStatus.REJECTED) {
            return LABEL_REJECTED;
        }
        if (request.getStatus() != RequestStatus.PRIORITIZED) {
            return LABEL_NEW;
        }
        return workflowRepository.findByRequest(request).map(CustomerAnalyticsData::workflowBucketLabel)
                .orElse(LABEL_PRIORITIZED);
    }

    private static String workflowBucketLabel(Workflow workflow) {
        return switch (workflow.getWorkflowStatus()) {
            case BACKLOG -> LABEL_BACKLOG;
            case IN_PROGRESS, TESTING -> LABEL_IN_PROGRESS;
            case DONE -> LABEL_DONE;
        };
    }

    static String averageResolutionTime(AppUser customer, RequestRepository requestRepository,
                                         RequestActivityRepository activityRepository) {
        List<Request> requests = requestRepository.findByCustomer_Email(customer.getEmail());
        OptionalDouble averageMillis = requests.stream()
                .map(r -> resolutionMillis(r, activityRepository))
                .filter(millis -> millis >= 0)
                .mapToLong(Long::longValue)
                .average();

        if (averageMillis.isEmpty()) {
            return "Henüz tamamlanmış talebiniz yok.";
        }
        Duration duration = Duration.ofMillis((long) averageMillis.getAsDouble());
        return duration.toDays() + " gün " + duration.toHoursPart() + " saat";
    }

    private static long resolutionMillis(Request request, RequestActivityRepository activityRepository) {
        List<RequestActivity> activities = activityRepository.findByRequestOrderByCreatedAtAsc(request);
        Instant created = activities.stream().filter(a -> "Talep oluşturuldu".equals(a.getDescription()))
                .map(RequestActivity::getCreatedAt).findFirst().orElse(null);
        Instant completed = activities.stream().filter(a -> "Tamamlandı".equals(a.getDescription()))
                .map(RequestActivity::getCreatedAt).findFirst().orElse(null);
        if (created == null || completed == null) {
            return -1;
        }
        return Duration.between(created, completed).toMillis();
    }
}
