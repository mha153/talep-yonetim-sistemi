package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.notifications.NotificationCenter;
import com.requestmanagement.base.ui.shared.ActivityRecorder;

/** State-transition actions the PO can take from the pending-requests kebab menu. */
final class RequestStatusActions {

    private RequestStatusActions() {
    }

    static void reject(Request request, RequestRepository requestRepository,
                        NotificationRepository notificationRepository,
                        RequestActivityRepository activityRepository, AppUser currentPo, String reason) {
        request.setStatus(RequestStatus.REJECTED);
        requestRepository.save(request);
        String reasonSuffix = reason == null || reason.isBlank() ? "" : " (" + reason + ")";
        ActivityRecorder.record(activityRepository, request, "Reddedildi" + reasonSuffix);
        NotificationCenter.notifyCustomer(notificationRepository, request, currentPo,
                "talebinizi reddetti" + reasonSuffix + ": " + request.getTitle());
    }

    static void convertToWorkflow(Request request, WorkflowRepository workflowRepository,
                                   UserRepository userRepository, NotificationRepository notificationRepository,
                                   RequestActivityRepository activityRepository, AppUser currentPo) {
        Workflow workflow = new Workflow();
        workflow.setRequest(request);
        workflowRepository.save(workflow);
        ActivityRecorder.record(activityRepository, request, "Sprint'e alındı");
        NotificationCenter.notifyAllDevelopers(notificationRepository, userRepository, request, currentPo,
                "talebi sprint havuzuna ekledi: " + request.getTitle());
    }
}
