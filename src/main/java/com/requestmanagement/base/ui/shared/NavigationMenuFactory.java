package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.archive.CompletedView;
import com.requestmanagement.base.ui.customer.CustomerRequestView;
import com.requestmanagement.base.ui.developer.MyTasksView;
import com.requestmanagement.base.ui.developer.SprintView;
import com.requestmanagement.base.ui.po.DashboardView;
import com.requestmanagement.base.ui.po.PendingRequestsView;
import com.requestmanagement.base.ui.po.SprintTrackingView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.menu.MenuEntry;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;

/**
 * Builds the role-based side navigation shown in {@link MainLayout}.
 */
final class NavigationMenuFactory {

    private NavigationMenuFactory() {
    }

    static SideNav buildSideNav(@Nullable Authentication auth, RequestRepository requestRepository,
                                 WorkflowRepository workflowRepository, UserRepository userRepository) {
        var nav = new SideNav();
        if (auth == null) {
            return nav;
        }

        boolean isCustomer = hasRole(auth, "ROLE_CUSTOMER");
        boolean isProductOwner = hasRole(auth, "ROLE_PRODUCT_OWNER");
        boolean isDeveloper = hasRole(auth, "ROLE_DEVELOPER");

        if (isCustomer) {
            nav.addItem(new SideNavItem("Taleplerim", CustomerRequestView.class));
        }

        if (isProductOwner) {
            nav.addItem(new SideNavItem("Genel Bakış", DashboardView.class));
            nav.addItem(new SideNavItem(NavigationBadges.withCount("Gelen Talepler",
                    NavigationBadges.pendingRequests(requestRepository, workflowRepository)),
                    PendingRequestsView.class));
            nav.addItem(new SideNavItem("Sprint Takibi", SprintTrackingView.class));
            nav.addItem(new SideNavItem("Arşiv", CompletedView.class));
        }

        if (isDeveloper) {
            nav.addItem(new SideNavItem(NavigationBadges.withCount("Sprint Havuzu",
                    NavigationBadges.sprintPool(workflowRepository)), SprintView.class));
            long myTasksCount = CurrentUserResolver.find(userRepository, auth)
                    .map(developer -> NavigationBadges.myTasks(workflowRepository, developer))
                    .orElse(0L);
            nav.addItem(new SideNavItem(NavigationBadges.withCount("Görevlerim", myTasksCount), MyTasksView.class));
            nav.addItem(new SideNavItem("Arşiv", CompletedView.class));
        }

        nav.addItem(new SideNavItem("Profil Ayarları", ProfileView.class));

        return nav;
    }

    private static boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    static SideNavItem toSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() == null) {
            return new SideNavItem(menuEntry.title(), menuEntry.menuClass());
        }
        Component icon = menuEntry.icon().contains(".svg")
                ? new SvgIcon(menuEntry.icon())
                : new Icon(menuEntry.icon());
        return new SideNavItem(menuEntry.title(), menuEntry.menuClass(), icon);
    }
}
