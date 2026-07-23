package com.requestmanagement.base.ui.messaging;

import com.requestmanagement.base.model.RequestMessage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Renders one chat message as a bubble: the viewer's own messages align right, others align left. */
final class MessageBubble {

    private static final String[] NAME_COLORS =
            {"#e53935", "#1e88e5", "#43a047", "#8e24aa", "#fb8c00", "#00897b"};
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    private MessageBubble() {
    }

    static Component create(RequestMessage message, Long viewerUserId) {
        boolean own = viewerUserId != null && viewerUserId.equals(message.getAuthor().getUserId());

        Span author = new Span(message.getAuthor().getNameSurname());
        author.getStyle().set("font-weight", "600").set("font-size", "0.8rem")
                .set("color", own ? "#ffd54f" : nameColor(message.getAuthor().getUserId()));

        Span body = new Span(message.getBody());
        body.getStyle().set("white-space", "pre-wrap").set("color", own ? "white" : "inherit");

        Span timestamp = new Span(TIMESTAMP_FORMAT.format(message.getCreatedAt()));
        timestamp.getStyle().set("font-size", "0.7rem")
                .set("color", own ? "rgba(255, 255, 255, 0.75)" : "var(--vaadin-text-color-secondary)");

        VerticalLayout bubble = new VerticalLayout(author, body, timestamp);
        bubble.setPadding(false);
        bubble.setSpacing(false);
        bubble.getStyle()
                .set("background", own ? "var(--aura-accent-color, #1e88e5)" : "var(--vaadin-background-container)")
                .set("border-radius", "10px")
                .set("padding", "6px 10px")
                .set("max-width", "80%")
                .set("align-self", own ? "flex-end" : "flex-start");
        return bubble;
    }

    private static String nameColor(Long userId) {
        return NAME_COLORS[(int) (userId % NAME_COLORS.length)];
    }
}
