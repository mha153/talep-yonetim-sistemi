package com.requestmanagement.base.ui.login;

import com.requestmanagement.base.ui.shared.BlankLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", layout = BlankLayout.class)
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");
        login.setI18n(buildTurkishI18n());
        login.setForgotPasswordButtonVisible(false);

        add(new H1("Talep Yönetim Sistemi"), login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean hasError = event.getLocation().getQueryParameters().getParameters().containsKey("error");
        login.setError(hasError);
    }

    private LoginI18n buildTurkishI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Giriş Yap");
        i18n.getForm().setUsername("Kullanıcı Adı");
        i18n.getForm().setPassword("Şifre");
        i18n.getForm().setSubmit("Giriş Yap");
        i18n.getForm().setForgotPassword("Şifremi Unuttum");
        i18n.getErrorMessage().setTitle("Giriş başarısız");
        i18n.getErrorMessage().setMessage("Kullanıcı adı veya şifre hatalı. Lütfen tekrar deneyin.");
        return i18n;
    }
}
