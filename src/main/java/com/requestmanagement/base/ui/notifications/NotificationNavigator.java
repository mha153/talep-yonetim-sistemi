package com.requestmanagement.base.ui.notifications;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.archive.CompletedView;
import com.requestmanagement.base.ui.customer.CustomerRequestView;
import com.requestmanagement.base.ui.developer.MyTasksView;
import com.requestmanagement.base.ui.developer.SprintView;
import com.requestmanagement.base.ui.po.PendingRequestsView;
import com.requestmanagement.base.ui.po.SprintTrackingView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.QueryParameters;

import java.util.Map;
import java.util.Optional;

/** Decides which screen currently shows a request for the viewer's role, then navigates there with a highlight hint. */
final class NotificationNavigator {

    private NotificationNavigator() {
    }

    static void open(Request request, AppUser currentUser, WorkflowRepository workflowRepository) {
        Class<? extends Component> target = resolveTarget(request, currentUser, workflowRepository);
        QueryParameters highlight =
                QueryParameters.simple(Map.of("highlight", String.valueOf(request.getRequestId())));
        UI.getCurrent().navigate(target, highlight);
    }

    private static Class<? extends Component> resolveTarget(Request request, AppUser currentUser,
                                                              WorkflowRepository workflowRepository) {
        if (Role.CUSTOMER.equals(currentUser.getRole())) {
            return CustomerRequestView.class;
        }
        Optional<Workflow> workflow = workflowRepository.findByRequest(request);
        boolean archived = RequestStatus.REJECTED.equals(request.getStatus())
                || workflow.map(w -> WorkflowStatus.DONE.equals(w.getWorkflowStatus())).orElse(false);
        if (archived) {
            return CompletedView.class;
        }
        if (Role.PRODUCT_OWNER.equals(currentUser.getRole())) {
            return workflow.isPresent() ? SprintTrackingView.class : PendingRequestsView.class;
        }
        boolean claimed = workflow.map(Workflow::getDeveloper).isPresent();
        return claimed ? MyTasksView.class : SprintView.class;
    }
}
