package com.requestmanagement.base.ui.notifications;

import com.requestmanagement.base.model.AppNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Renders one {@link AppNotification}: actor (bold) + action (muted) + request title (accent), with a timestamp. */
final class NotificationRow {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    private NotificationRow() {
    }

    static Component create(AppNotification notification) {
        VerticalLayout row = new VerticalLayout(messageLine(notification), timestamp(notification));
        row.setPadding(false);
        row.setSpacing(false);
        return row;
    }

    private static Component messageLine(AppNotification notification) {
        if (notification.getActor() == null) {
            return new Span(notification.getMessage());
        }
        Span actorName = new Span(notification.getActor().getNameSurname());
        actorName.getStyle().set("font-weight", "600");

        String message = notification.getMessage();
        int separatorIndex = message.indexOf(": ");
        if (separatorIndex < 0) {
            return new HorizontalLayout(actorName, new Span(message));
        }
        Span action = new Span(message.substring(0, separatorIndex + 1));
        action.getStyle().set("color", "var(--vaadin-text-color-secondary)");
        Span title = new Span(message.substring(separatorIndex + 2));
        title.getStyle().set("color", "var(--aura-accent-color)").set("font-weight", "500");

        HorizontalLayout line = new HorizontalLayout(actorName, action, title);
        line.setSpacing(false);
        line.getStyle().set("gap", "4px").set("flex-wrap", "wrap");
        return line;
    }

    private static Component timestamp(AppNotification notification) {
        Span timestamp = new Span(TIMESTAMP_FORMAT.format(notification.getCreatedAt()));
        timestamp.getStyle().set("color", "var(--vaadin-text-color-secondary)").set("font-size", "0.75rem");
        return timestamp;
    }
}
