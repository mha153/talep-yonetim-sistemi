package com.requestmanagement.base.ui;

import com.vaadin.flow.component.notification.Notification;

/** Shows a toast in a spot that never overlaps the navigation drawer's footer. */
final class Toast {

    private Toast() {
    }

    static void show(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER);
    }
}
