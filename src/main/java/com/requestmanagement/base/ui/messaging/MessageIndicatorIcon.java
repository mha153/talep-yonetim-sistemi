package com.requestmanagement.base.ui.messaging;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/** Suffixes a component (typically a message button) with a small chat-bubble icon when there's an unread message. */
public final class MessageIndicatorIcon {

    private MessageIndicatorIcon() {
    }

    public static Component wrap(Component target, boolean hasUnread) {
        if (!hasUnread) {
            return target;
        }
        Icon icon = VaadinIcon.COMMENT.create();
        icon.setSize("12px");
        icon.getStyle().set("color", "var(--lumo-primary-color)");
        HorizontalLayout layout = new HorizontalLayout(target, icon);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        return layout;
    }
}
