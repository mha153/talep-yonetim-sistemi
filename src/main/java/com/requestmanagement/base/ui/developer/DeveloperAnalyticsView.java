package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.BarChart;
import com.requestmanagement.base.ui.shared.CurrentUserResolver;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.requestmanagement.base.ui.shared.PieChart;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

/** The developer's own analytics page: task status pie chart plus effort distribution bar chart. */
@Route(value = "dev-analytics", layout = MainLayout.class)
@PageTitle("Analiz")
@RolesAllowed("DEVELOPER")
public class DeveloperAnalyticsView extends VerticalLayout {

    public DeveloperAnalyticsView(WorkflowRepository workflowRepository,
                                   PrioritizationRepository prioritizationRepository, UserRepository userRepository) {
        AppUser developer = CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.DEVELOPER);

        VerticalLayout pieSection = new VerticalLayout(
                PieChart.create(DeveloperAnalyticsData.statusSlices(developer, workflowRepository)));
        pieSection.setPadding(false);
        pieSection.setHeightFull();

        setSizeFull();
        setPadding(true);
        add(pieSection, new H4("Efor Dağılımı"),
                BarChart.create(DeveloperAnalyticsData.effortBars(developer, workflowRepository,
                        prioritizationRepository)));
    }
}
