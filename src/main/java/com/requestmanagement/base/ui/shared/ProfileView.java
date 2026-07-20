package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.repository.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Lets the current user view their account info, rename themselves, and change their password. */
@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profil Ayarları")
@PermitAll
public class ProfileView extends VerticalLayout {

    public ProfileView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        AppUser currentUser = CurrentUserResolver.find(userRepository,
                SecurityContextHolder.getContext().getAuthentication()).orElseThrow();

        Span emailLabel = new Span("Email: " + currentUser.getEmail());
        Span roleLabel = new Span("Rol: " + currentUser.getRole().displayLabel());

        TextField nameField = new TextField("Ad Soyad");
        nameField.setValue(currentUser.getNameSurname());
        nameField.setWidth("320px");
        Button saveNameButton = new Button("Adı Kaydet", e -> {
            currentUser.setNameSurname(nameField.getValue());
            userRepository.save(currentUser);
            Toast.show("Ad soyad güncellendi.");
        });
        saveNameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        setPadding(true);
        add(new H3("Profil Ayarları"), emailLabel, roleLabel, nameField, saveNameButton,
                new PasswordChangeForm(currentUser, userRepository, passwordEncoder));
    }
}
