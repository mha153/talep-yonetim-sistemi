package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.EmailChangeRequest;
import com.requestmanagement.base.model.RegistrationRequest;
import com.requestmanagement.base.repository.EmailChangeRequestRepository;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RegistrationRequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.ArrayList;
import java.util.List;

/** Lets the PO approve or reject pending sign-up and e-posta change requests in one combined list. */
class PendingAccountRequestsGrid extends Grid<PendingAccountRequestsGrid.Row> {

    record Row(String type, String user, String current, String requested, Runnable approve, Runnable reject) {
    }

    private final transient RegistrationRequestRepository registrationRequestRepository;
    private final transient EmailChangeRequestRepository emailChangeRequestRepository;
    private final transient UserRepository userRepository;
    private final transient NotificationRepository notificationRepository;
    private final transient AppUser currentPo;

    PendingAccountRequestsGrid(RegistrationRequestRepository registrationRequestRepository,
                                EmailChangeRequestRepository emailChangeRequestRepository,
                                UserRepository userRepository, NotificationRepository notificationRepository,
                                AppUser currentPo) {
        super(Row.class, false);
        this.registrationRequestRepository = registrationRequestRepository;
        this.emailChangeRequestRepository = emailChangeRequestRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.currentPo = currentPo;
        setWidthFull();
        setHeight("260px");

        addColumn(Row::type).setHeader("Tür").setWidth("160px").setFlexGrow(0);
        addColumn(Row::user).setHeader("Kullanıcı").setFlexGrow(1);
        addColumn(Row::current).setHeader("Mevcut").setFlexGrow(1);
        addColumn(Row::requested).setHeader("Talep Edilen").setFlexGrow(1);
        addComponentColumn(this::buildActions).setHeader("İşlem").setWidth("180px").setFlexGrow(0);

        refresh();
    }

    void refresh() {
        List<Row> rows = new ArrayList<>();
        registrationRequestRepository.findAll().forEach(r -> rows.add(registrationRow(r)));
        emailChangeRequestRepository.findAll().forEach(r -> rows.add(emailChangeRow(r)));
        setItems(rows);
    }

    private Row registrationRow(RegistrationRequest registration) {
        return new Row("Yeni Kayıt", registration.getNameSurname(), "—", registration.getRequestedEmail(),
                () -> PendingAccountRequestActions.approveRegistration(registration,
                        registrationRequestRepository, userRepository, this::refresh),
                () -> PendingAccountRequestActions.rejectRegistration(registration,
                        registrationRequestRepository, this::refresh));
    }

    private Row emailChangeRow(EmailChangeRequest changeRequest) {
        return new Row("E-posta Değişikliği", changeRequest.getUser().getNameSurname(),
                changeRequest.getUser().getEmail(), changeRequest.getRequestedEmail(),
                () -> PendingAccountRequestActions.approveEmailChange(changeRequest,
                        emailChangeRequestRepository, userRepository, notificationRepository, currentPo,
                        this::refresh),
                () -> PendingAccountRequestActions.rejectEmailChange(changeRequest,
                        emailChangeRequestRepository, notificationRepository, currentPo, this::refresh));
    }

    private HorizontalLayout buildActions(Row row) {
        Button approveButton = new Button("Onayla", e -> row.approve().run());
        approveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        Button rejectButton = new Button("Reddet", e -> row.reject().run());
        rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_TERTIARY);
        return new HorizontalLayout(approveButton, rejectButton);
    }
}
