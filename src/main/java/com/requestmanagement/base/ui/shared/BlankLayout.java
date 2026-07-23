package com.requestmanagement.base.ui.shared;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * A chrome-free layout (no menu/header) used by the public login/register screens.
 * Must allow at least as much access as its broadest child view (here, {@code @AnonymousAllowed}),
 * otherwise Vaadin's view access checker denies navigation to that child.
 */
@AnonymousAllowed
public class BlankLayout extends VerticalLayout implements RouterLayout {

    public BlankLayout() {
        setSizeFull();
        setPadding(false);
        setMargin(false);
    }
}
