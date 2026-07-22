package com.requestmanagement.base.ui.shared;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;

import java.util.function.Function;

/** Marks the row matching {@code highlightId} with an accent background, used when arriving from a notification. */
public final class GridRowHighlighter {

    private static final String HIGHLIGHT_PART = "highlighted-row";

    private GridRowHighlighter() {
    }

    public static <T> void apply(Grid<T> grid, Function<T, Long> idExtractor, Long highlightId) {
        if (highlightId == null) {
            return;
        }
        UI.getCurrent().getPage().executeJs(
                "if (!document.getElementById('notification-highlight-style')) {"
                        + "const s = document.createElement('style');"
                        + "s.id = 'notification-highlight-style';"
                        + "s.textContent = 'vaadin-grid::part(" + HIGHLIGHT_PART + "){background:"
                        + "var(--aura-accent-color-light,#cce4ff) !important;}';"
                        + "document.head.appendChild(s);"
                        + "document.addEventListener('click', () => {"
                        + "const style = document.getElementById('notification-highlight-style');"
                        + "if (style) { style.remove(); }"
                        + "}, {once: true});"
                        + "}");
        grid.setPartNameGenerator(item -> highlightId.equals(idExtractor.apply(item)) ? HIGHLIGHT_PART : null);
    }
}
