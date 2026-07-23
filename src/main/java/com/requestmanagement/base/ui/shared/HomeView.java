package com.requestmanagement.base.ui.shared;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/** The landing page shown at the root URL after login, before a role-specific page is picked from the menu. */
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H2("Talep Yönetim Sistemine Hoş Geldiniz"));
        add(new Span("Lütfen işlem yapmak için sol menüyü kullanın."));
    }
}
