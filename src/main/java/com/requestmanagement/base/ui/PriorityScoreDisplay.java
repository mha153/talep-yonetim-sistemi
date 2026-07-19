package com.requestmanagement.base.ui;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/** The live "HESAPLANAN SKOR" box shown in {@link PrioritizationDialog}. */
class PriorityScoreDisplay extends VerticalLayout {

    private final Span scoreLabel = new Span("-");
    private final Span priorityBadge = new Span();

    PriorityScoreDisplay() {
        scoreLabel.getStyle().set("font-size", "var(--lumo-font-size-xxl)").set("font-weight", "bold");
        priorityBadge.getElement().getThemeList().add("badge");

        setAlignItems(Alignment.CENTER);
        getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-m)");
        add(new H4("HESAPLANAN SKOR"), scoreLabel, priorityBadge);
    }

    void update(Integer impact, Integer urgency) {
        if (impact == null || urgency == null) {
            scoreLabel.setText("-");
            priorityBadge.setText("");
            return;
        }
        int score = RequestScoreBadge.compute(impact, urgency);
        scoreLabel.setText(String.valueOf(score));
        scoreLabel.getStyle().set("color", RequestScoreBadge.color(score));
        priorityBadge.getElement().getThemeList().remove("error");
        priorityBadge.getElement().getThemeList().remove("warning");
        priorityBadge.getElement().getThemeList().remove("success");
        priorityBadge.setText(RequestScoreBadge.label(score));
        priorityBadge.getElement().getThemeList().add(RequestScoreBadge.theme(score));
    }
}
