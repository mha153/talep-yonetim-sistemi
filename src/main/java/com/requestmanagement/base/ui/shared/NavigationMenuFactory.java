package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.ui.archive.CompletedView;
import com.requestmanagement.base.ui.customer.CustomerRequestView;
import com.requestmanagement.base.ui.developer.MyTasksView;
import com.requestmanagement.base.ui.developer.SprintView;
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

    static SideNav buildSideNav(@Nullable Authentication auth) {
        var nav = new SideNav();
        if (auth == null) {
            return nav;
        }

        boolean isCustomer = hasRole(auth, "ROLE_CUSTOMER");
        boolean isProductOwner = hasRole(auth, "ROLE_PRODUCT_OWNER");
        boolean isDeveloper = hasRole(auth, "ROLE_DEVELOPER");

        if (isCustomer) {
            nav.addItem(new SideNavItem("Talep Oluştur", CustomerRequestView.class));
        }

        if (isProductOwner) {
            nav.addItem(new SideNavItem("Gelen Talepler", PendingRequestsView.class));
            nav.addItem(new SideNavItem("Sprint Takibi", SprintTrackingView.class));
            nav.addItem(new SideNavItem("Arşiv", CompletedView.class));
        }

        if (isDeveloper) {
            nav.addItem(new SideNavItem("Sprint Havuzu", SprintView.class));
            nav.addItem(new SideNavItem("Görevlerim", MyTasksView.class));
            nav.addItem(new SideNavItem("Arşiv", CompletedView.class));
        }

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
