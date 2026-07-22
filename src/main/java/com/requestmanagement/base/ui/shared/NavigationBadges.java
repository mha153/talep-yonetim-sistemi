package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.WorkflowRepository;

import java.util.List;

/** Computes the pending-work counts shown next to side-navigation labels. */
final class NavigationBadges {

    private static final List<RequestStatus> PENDING_STATUSES =
            List.of(RequestStatus.NEW, RequestStatus.UNDER_REVIEW, RequestStatus.PRIORITIZED);

    private NavigationBadges() {
    }

    static String withCount(String label, long count) {
        return count > 0 ? label + " (" + count + ")" : label;
    }

    static long pendingRequests(RequestRepository requestRepository, WorkflowRepository workflowRepository) {
        return requestRepository.findByStatusIn(PENDING_STATUSES).stream()
                .filter(r -> !workflowRepository.existsByRequest(r))
                .count();
    }

    static long sprintPool(WorkflowRepository workflowRepository) {
        return workflowRepository.findByDeveloperIsNullAndWorkflowStatusNot(WorkflowStatus.DONE).size();
    }

    static long myTasks(WorkflowRepository workflowRepository, AppUser developer) {
        return workflowRepository.findByDeveloperAndWorkflowStatusNot(developer, WorkflowStatus.DONE).size();
    }
}
