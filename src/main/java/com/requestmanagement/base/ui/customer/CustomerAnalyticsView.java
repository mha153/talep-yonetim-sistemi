package com.requestmanagement.base.ui.customer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.CurrentUserResolver;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.requestmanagement.base.ui.shared.PieChart;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

/** The customer's own analytics page: request status pie chart plus average resolution time. */
@Route(value = "my-analytics", layout = MainLayout.class)
@PageTitle("Analiz")
@RolesAllowed("CUSTOMER")
public class CustomerAnalyticsView extends VerticalLayout {

    public CustomerAnalyticsView(RequestRepository requestRepository, WorkflowRepository workflowRepository,
                                  RequestActivityRepository activityRepository, UserRepository userRepository) {
        AppUser customer = CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.CUSTOMER);

        H4 averageTimeLabel = new H4("Ortalama Çözüm Süresi: "
                + CustomerAnalyticsData.averageResolutionTime(customer, requestRepository, activityRepository));

        VerticalLayout pieSection = new VerticalLayout(
                PieChart.create(CustomerAnalyticsData.statusSlices(customer, requestRepository, workflowRepository)));
        pieSection.setPadding(false);
        pieSection.setHeightFull();

        setSizeFull();
        setPadding(true);
        add(averageTimeLabel, pieSection);
    }
}
