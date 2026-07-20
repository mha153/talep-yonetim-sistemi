package com.requestmanagement.base.ui.shared;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/** Toggles the dark theme for the current session only (not persisted). */
class ThemeToggle extends Button {

    private boolean dark;

    ThemeToggle() {
        super(new Icon(VaadinIcon.MOON));
        addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        addClickListener(e -> toggle());
    }

    private void toggle() {
        dark = !dark;
        // Aura's palette is built on CSS light-dark(), which reads the color-scheme
        // property, not a theme attribute — so that's what has to be set here.
        String script = dark
                ? "document.documentElement.style.colorScheme = 'dark'"
                : "document.documentElement.style.colorScheme = ''";
        UI.getCurrent().getPage().executeJs(script);
        setIcon(new Icon(dark ? VaadinIcon.SUN_O : VaadinIcon.MOON));
    }
}
