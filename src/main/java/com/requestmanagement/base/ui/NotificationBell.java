package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.AppNotification;
import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.repository.NotificationRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/** A bell button showing the unread-notification count; opens a dialog and marks them all read. */
class NotificationBell extends Button {

    NotificationBell(NotificationRepository notificationRepository, AppUser currentUser) {
        super(new Icon(VaadinIcon.BELL));
        addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        updateBadge(notificationRepository, currentUser);
        addClickListener(e -> openDialog(notificationRepository, currentUser));
    }

    private void updateBadge(NotificationRepository notificationRepository, AppUser currentUser) {
        long unread = notificationRepository.countByRecipientAndReadFalse(currentUser);
        setText(unread > 0 ? String.valueOf(unread) : "");
    }

    private void openDialog(NotificationRepository notificationRepository, AppUser currentUser) {
        List<AppNotification> notifications =
                notificationRepository.findByRecipientOrderByCreatedAtDesc(currentUser);

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Bildirimler");
        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        if (notifications.isEmpty()) {
            list.add(new Span("Bildirim yok."));
        } else {
            notifications.forEach(n -> list.add(notificationRow(n)));
        }
        dialog.add(list);
        dialog.open();

        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
        updateBadge(notificationRepository, currentUser);
    }

    private Component notificationRow(AppNotification notification) {
        if (notification.getActor() == null) {
            return new Span(notification.getMessage());
        }
        Span actorName = new Span(notification.getActor().getNameSurname());
        actorName.getStyle().set("font-weight", "600");
        HorizontalLayout row = new HorizontalLayout(actorName, new Span(notification.getMessage()));
        row.setSpacing(false);
        row.getStyle().set("gap", "4px");
        return row;
    }
}
