package com.requestmanagement.base.ui.shared;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/** Renders a set of labeled counts as a pure-CSS pie chart (no charting library needed). */
public final class PieChart {

    private PieChart() {
    }

    public record Slice(String label, long count, String color) {
    }

    public static Component create(List<Slice> slices) {
        long total = slices.stream().mapToLong(Slice::count).sum();

        Div pie = new Div();
        pie.getStyle().set("width", "380px").set("height", "380px").set("border-radius", "50%")
                .set("background", buildGradient(slices, total)).set("flex-shrink", "0")
                .set("box-shadow", "var(--lumo-box-shadow-s)");

        VerticalLayout legend = new VerticalLayout();
        legend.setPadding(false);
        slices.forEach(slice -> legend.add(legendRow(slice)));

        HorizontalLayout layout = new HorizontalLayout(pie, legend);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setSizeFull();
        layout.getStyle().set("gap", "64px");
        return layout;
    }

    private static String buildGradient(List<Slice> slices, long total) {
        if (total == 0) {
            return "var(--lumo-contrast-10pct)";
        }
        StringBuilder gradient = new StringBuilder("conic-gradient(");
        double cursor = 0;
        boolean first = true;
        for (Slice slice : slices) {
            if (slice.count() == 0) {
                continue;
            }
            double sliceDeg = 360.0 * slice.count() / total;
            if (!first) {
                gradient.append(", ");
            }
            gradient.append(slice.color()).append(' ').append(cursor).append("deg ")
                    .append(cursor + sliceDeg).append("deg");
            cursor += sliceDeg;
            first = false;
        }
        gradient.append(')');
        return gradient.toString();
    }

    private static Component legendRow(Slice slice) {
        Div swatch = new Div();
        swatch.getStyle().set("width", "22px").set("height", "22px").set("border-radius", "4px")
                .set("background", slice.color()).set("flex-shrink", "0");

        Span text = new Span(slice.label() + ": " + slice.count());
        text.getStyle().set("font-size", "1.3rem");

        HorizontalLayout row = new HorizontalLayout(swatch, text);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("gap", "12px");
        return row;
    }
}
