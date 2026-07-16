package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.repository.UserRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.ScrollerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

@Layout
@PermitAll
public final class MainLayout extends AppLayout {

    private final transient AuthenticationContext authContext;
    private final transient UserRepository userRepository;

    public MainLayout(AuthenticationContext authContext, UserRepository userRepository) {
        this.authContext = authContext;
        this.userRepository = userRepository;
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
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var scroller = new Scroller(NavigationMenuFactory.buildSideNav(auth));
        scroller.addThemeVariants(ScrollerVariant.OVERFLOW_INDICATORS);
        return scroller;
    }

    private Component createApplicationFooter() {
        Span currentUserLabel = new Span(currentUserDisplayText());
        currentUserLabel.getStyle().set("white-space", "normal").set("text-align", "center");

        Button logoutButton = new Button("Çıkış Yap", e -> authContext.logout());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        logoutButton.setWidthFull();

        var footer = new VerticalLayout(currentUserLabel, logoutButton);
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.addClassName("app-footer");
        return footer;
    }

    private String currentUserDisplayText() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return "";
        }
        return CurrentUserResolver.find(userRepository, auth)
                .map(this::formatUser)
                .orElse("Giriş Yapan: " + auth.getName());
    }

    private String formatUser(AppUser user) {
        return "Giriş Yapan: " + user.getNameSurname() + " (" + user.getRole().displayLabel() + ")";
    }
}
