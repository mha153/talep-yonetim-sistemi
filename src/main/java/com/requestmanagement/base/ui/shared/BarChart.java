package com.requestmanagement.base.ui.shared;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/** Renders a set of labeled counts as a pure-CSS horizontal bar chart (no charting library needed). */
public final class BarChart {

    private BarChart() {
    }

    public record Bar(String label, long value, String color) {
    }

    public static Component create(List<Bar> bars) {
        long max = bars.stream().mapToLong(Bar::value).max().orElse(0);
        VerticalLayout chart = new VerticalLayout();
        chart.setPadding(false);
        chart.setSpacing(false);
        bars.forEach(bar -> chart.add(barRow(bar, max)));
        return chart;
    }

    private static Component barRow(Bar bar, long max) {
        Span label = new Span(bar.label() + ": " + bar.value());
        label.getStyle().set("font-size", "0.95rem");

        Div fill = new Div();
        double percent = max == 0 ? 0 : bar.value() * 100.0 / max;
        fill.getStyle().set("width", percent + "%").set("height", "100%")
                .set("background", bar.color()).set("border-radius", "4px");

        Div track = new Div(fill);
        track.getStyle().set("width", "100%").set("height", "18px")
                .set("background", "var(--vaadin-background-container)")
                .set("border-radius", "4px").set("overflow", "hidden");

        VerticalLayout row = new VerticalLayout(label, track);
        row.setPadding(false);
        row.setSpacing(false);
        row.getStyle().set("margin-bottom", "10px");
        return row;
    }
}
