package com.talep.base.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

public class BlankLayout extends VerticalLayout implements RouterLayout {
    
    public BlankLayout() {
        setSizeFull();
        setPadding(false);
        setMargin(false);
    }
}