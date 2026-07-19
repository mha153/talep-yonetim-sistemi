package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.repository.NotificationRepository;
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

import java.util.Optional;

@Layout
@PermitAll
public final class MainLayout extends AppLayout {

    private final transient AuthenticationContext authContext;
    private final transient UserRepository userRepository;
    private final transient NotificationRepository notificationRepository;

    public MainLayout(AuthenticationContext authContext, UserRepository userRepository,
                       NotificationRepository notificationRepository) {
        this.authContext = authContext;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        setPrimarySection(Section.DRAWER);
        addToDrawer(createApplicationHeader(), createApplicationDrawer(), createApplicationFooter());
    }

    private Component createApplicationHeader() {
        var appLogo = new Avatar("Talep Yönetimi");
        appLogo.addClassName("app-logo");
        appLogo.addThemeVariants(AvatarVariant.AURA_FILLED, AvatarVariant.XSMALL);

        var appName = new Span("Talep Yönetimi");
        appName.addClassName("app-name");

        var header = new HorizontalLayout(appLogo, appName, new ThemeToggle());
        currentUser().ifPresent(user -> header.add(new NotificationBell(notificationRepository, user)));
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

    private Optional<AppUser> currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? Optional.empty() : CurrentUserResolver.find(userRepository, auth);
    }

    private String currentUserDisplayText() {
        return currentUser().map(this::formatUser).orElse("");
    }

    private String formatUser(AppUser user) {
        return "Giriş Yapan: " + user.getNameSurname() + " (" + user.getRole().displayLabel() + ")";
    }
}
