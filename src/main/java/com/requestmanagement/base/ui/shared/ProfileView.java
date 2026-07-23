package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.repository.EmailChangeRequestRepository;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Lets the current user view their account info, rename themselves, and change their password/e-posta. */
@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profil Ayarları")
@PermitAll
public class ProfileView extends VerticalLayout {

    public ProfileView(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        EmailChangeRequestRepository emailChangeRequestRepository,
                        NotificationRepository notificationRepository) {
        AppUser currentUser = CurrentUserResolver.find(userRepository,
                SecurityContextHolder.getContext().getAuthentication()).orElseThrow();

        Span emailLabel = new Span("Email: " + currentUser.getEmail());
        Span roleLabel = new Span("Rol: " + currentUser.getRole().displayLabel());
        Button changeEmailButton = new Button("E-posta Değiştir", e -> new EmailChangeDialog(
                currentUser, userRepository, emailChangeRequestRepository, notificationRepository).open());
        changeEmailButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        HorizontalLayout emailRow = new HorizontalLayout(emailLabel, changeEmailButton);
        emailRow.setAlignItems(FlexComponent.Alignment.CENTER);

        TextField nameField = new TextField("Ad Soyad");
        nameField.setValue(currentUser.getNameSurname());
        nameField.setWidth("320px");
        Button saveNameButton = new Button("Adı Kaydet", e -> {
            currentUser.setNameSurname(nameField.getValue());
            userRepository.save(currentUser);
            UI.getCurrent().getPage().reload();
        });
        saveNameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        setPadding(true);
        add(new H3("Profil Ayarları"), emailRow, roleLabel, nameField, saveNameButton,
                new PasswordChangeForm(currentUser, userRepository, passwordEncoder));
    }
}
