package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.notifications.NotificationCenter;
import com.requestmanagement.base.ui.shared.ActivityRecorder;

/** State-transition actions available to a developer from the Sprint queue. */
final class WorkflowActions {

    private WorkflowActions() {
    }

    static void claim(Workflow workflow, AppUser developer, WorkflowRepository workflowRepository,
                       UserRepository userRepository, NotificationRepository notificationRepository,
                       RequestActivityRepository activityRepository) {
        workflow.setDeveloper(developer);
        workflow.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);
        workflowRepository.save(workflow);
        ActivityRecorder.record(activityRepository, workflow.getRequest(),
                developer.getNameSurname() + " tarafından üstlenildi");
        NotificationCenter.notifyProductOwner(notificationRepository, userRepository, workflow.getRequest(),
                developer, "bir görevi üstlendi: " + workflow.getRequest().getTitle());
    }

    static void release(Workflow workflow, AppUser developer, WorkflowRepository workflowRepository,
                         UserRepository userRepository, NotificationRepository notificationRepository,
                         RequestActivityRepository activityRepository) {
        workflow.setDeveloper(null);
        workflow.setWorkflowStatus(WorkflowStatus.BACKLOG);
        workflowRepository.save(workflow);
        ActivityRecorder.record(activityRepository, workflow.getRequest(),
                developer.getNameSurname() + " görevi havuza geri bıraktı");
        NotificationCenter.notifyProductOwner(notificationRepository, userRepository, workflow.getRequest(),
                developer, "bir görevi havuza geri bıraktı: " + workflow.getRequest().getTitle());
    }

    static void startTesting(Workflow workflow, AppUser developer, WorkflowRepository workflowRepository,
                              UserRepository userRepository, NotificationRepository notificationRepository,
                              RequestActivityRepository activityRepository) {
        workflow.setWorkflowStatus(WorkflowStatus.TESTING);
        workflowRepository.save(workflow);
        ActivityRecorder.record(activityRepository, workflow.getRequest(), "Test aşamasına alındı");
        NotificationCenter.notifyProductOwner(notificationRepository, userRepository, workflow.getRequest(),
                developer, "bir görevi test aşamasına aldı: " + workflow.getRequest().getTitle());
    }

    static void complete(Workflow workflow, AppUser developer, WorkflowRepository workflowRepository,
                          NotificationRepository notificationRepository,
                          RequestActivityRepository activityRepository) {
        workflow.setWorkflowStatus(WorkflowStatus.DONE);
        workflowRepository.save(workflow);
        ActivityRecorder.record(activityRepository, workflow.getRequest(), "Tamamlandı");
        NotificationCenter.notifyCustomer(notificationRepository, workflow.getRequest(), developer,
                "talebinizi tamamladı: " + workflow.getRequest().getTitle());
    }
}
