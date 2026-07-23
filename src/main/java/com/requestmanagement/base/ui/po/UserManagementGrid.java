package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.EmailChangeRequestRepository;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.DeleteConfirmationDialog;
import com.requestmanagement.base.ui.shared.Toast;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;

/** Table letting the PO see every user, change their role, deactivate them, or remove unused accounts. */
class UserManagementGrid extends Grid<AppUser> {

    private final transient UserRepository userRepository;
    private final transient RequestRepository requestRepository;
    private final transient WorkflowRepository workflowRepository;
    private final transient NotificationRepository notificationRepository;
    private final transient RequestMessageRepository messageRepository;
    private final transient EmailChangeRequestRepository emailChangeRequestRepository;
    private final transient AppUser currentPo;

    UserManagementGrid(UserRepository userRepository, RequestRepository requestRepository,
                        WorkflowRepository workflowRepository, NotificationRepository notificationRepository,
                        RequestMessageRepository messageRepository,
                        EmailChangeRequestRepository emailChangeRequestRepository, AppUser currentPo) {
        super(AppUser.class, false);
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.workflowRepository = workflowRepository;
        this.notificationRepository = notificationRepository;
        this.messageRepository = messageRepository;
        this.emailChangeRequestRepository = emailChangeRequestRepository;
        this.currentPo = currentPo;
        setSizeFull();

        addColumn(AppUser::getUserId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        addColumn(AppUser::getNameSurname).setHeader("Ad Soyad").setFlexGrow(1);
        addColumn(AppUser::getEmail).setHeader("Email").setFlexGrow(2);
        addColumn(user -> user.isActive() ? "Aktif" : "Pasif").setHeader("Durum").setWidth("90px").setFlexGrow(0);
        addComponentColumn(this::buildRoleSelect).setHeader("Rol").setWidth("230px").setFlexGrow(0);
        addComponentColumn(this::buildActions).setHeader("İşlem").setWidth("230px").setFlexGrow(0);

        refresh();
    }

    void refresh() {
        setItems(userRepository.findAll());
    }

    private Select<Role> buildRoleSelect(AppUser user) {
        Select<Role> select = new Select<>();
        select.setItems(Role.values());
        select.setItemLabelGenerator(Role::displayLabel);
        select.setValue(user.getRole());
        select.setEnabled(!user.getUserId().equals(currentPo.getUserId()));
        select.addValueChangeListener(e -> {
            user.setRole(e.getValue());
            userRepository.save(user);
            Toast.show("Rol güncellendi.");
        });
        return select;
    }

    private HorizontalLayout buildActions(AppUser user) {
        boolean self = user.getUserId().equals(currentPo.getUserId());

        Button toggleButton = new Button(user.isActive() ? "Pasifleştir" : "Aktifleştir",
                e -> UserManagementActions.toggleActive(user, userRepository, workflowRepository, this::refresh));
        toggleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        toggleButton.setEnabled(!self);

        Button deleteButton = new Button("Sil", e -> new DeleteConfirmationDialog(
                "Bu kullanıcıyı silmek istediğinize emin misiniz?",
                () -> UserManagementActions.delete(user, userRepository, requestRepository, workflowRepository,
                        notificationRepository, messageRepository, emailChangeRequestRepository, this::refresh))
                .open());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_TERTIARY);
        deleteButton.setEnabled(!self);

        return new HorizontalLayout(toggleButton, deleteButton);
    }
}
