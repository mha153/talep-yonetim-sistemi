package com.requestmanagement.base.ui.notifications;

import com.requestmanagement.base.model.AppNotification;
import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.repository.NotificationRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/** A bell button showing the unread-notification count; opens a dialog and marks them all read. */
public class NotificationBell extends Button {

    private static final int COLLAPSED_LIMIT = 5;

    public NotificationBell(NotificationRepository notificationRepository, AppUser currentUser) {
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
        renderList(list, notifications, false);
        dialog.add(list);
        dialog.open();

        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
        updateBadge(notificationRepository, currentUser);
    }

    private void renderList(VerticalLayout list, List<AppNotification> notifications, boolean showAll) {
        list.removeAll();
        if (notifications.isEmpty()) {
            list.add(new Span("Bildirim yok."));
            return;
        }
        List<AppNotification> visible =
                showAll ? notifications : notifications.stream().limit(COLLAPSED_LIMIT).toList();
        visible.forEach(n -> list.add(NotificationRow.create(n)));

        if (!showAll && notifications.size() > COLLAPSED_LIMIT) {
            Button showAllButton = new Button("Tümünü Göster", e -> renderList(list, notifications, true));
            showAllButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            list.add(showAllButton);
        }
    }
}
