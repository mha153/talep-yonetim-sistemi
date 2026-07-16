package com.talep.base.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

@Layout
@PermitAll
public final class MainLayout extends AppLayout {

    private final transient AuthenticationContext authContext;

    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;
        setPrimarySection(Section.DRAWER);
        addToDrawer(createApplicationHeader(), createApplicationDrawer(), createApplicationFooter());
    }

    private Component createApplicationHeader() {
        var appLogo = new Avatar("Talep Yönetimi");
        appLogo.addClassName("app-logo");
        appLogo.addThemeVariants(AvatarVariant.AURA_FILLED, AvatarVariant.XSMALL);

        var appName = new Span("Talep Yönetimi");
        appName.addClassName("app-name");

        var header = new HorizontalLayout(appLogo, appName);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        return header;
    }

    private Component createApplicationDrawer() {
        var scroller = new Scroller(createSideNav());
        scroller.addThemeVariants(ScrollerVariant.OVERFLOW_INDICATORS);
        return scroller;
    }

    private Component createApplicationFooter() {
        Button cikisButonu = new Button("Çıkış Yap", e -> authContext.logout());
        cikisButonu.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        cikisButonu.setWidthFull();

        var footer = new VerticalLayout(cikisButonu, new Span("Made with ❤️ with Vaadin"));
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.addClassName("app-footer");
        return footer;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            boolean isCustomer = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
            boolean isProductOwner = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PRODUCT_OWNER"));
            boolean isDeveloper = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DEVELOPER"));

            if (isCustomer) {
                nav.addItem(new SideNavItem("Talep Oluştur", TalepView.class));
            }

            if (isProductOwner) {
                nav.addItem(new SideNavItem("Gelen Talepler", GelenTaleplerView.class));
                nav.addItem(new SideNavItem("Arşiv", CompletedView.class));
            }

            if (isDeveloper) {
                nav.addItem(new SideNavItem("Sprint", SprintView.class));
                nav.addItem(new SideNavItem("Arşiv", CompletedView.class));
            }
        }
        
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            Component icon = null;
            if (menuEntry.icon().contains(".svg")) {
                icon = new SvgIcon(menuEntry.icon());
            } else {
                icon = new Icon(menuEntry.icon());
            }
            return new SideNavItem(menuEntry.title(), menuEntry.menuClass(), icon);
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.menuClass());
        }
    }
}