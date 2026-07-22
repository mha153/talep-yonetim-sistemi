package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.Prioritization;
import com.vaadin.flow.component.html.Span;

/** Shared score formula/theme/label mapping used by the pending-requests grid and prioritization dialog. */
public final class RequestScoreBadge {

    private RequestScoreBadge() {
    }

    public static int compute(int impact, int urgency) {
        return impact * urgency * 4;
    }

    public static Span create(Prioritization prioritization) {
        Span badge = new Span();
        if (prioritization == null) {
            badge.setText("Belirlenmedi");
            return badge;
        }
        int score = prioritization.getPriorityScore();
        badge.setText(score + " (" + shortLabel(score) + ")");
        badge.getElement().getThemeList().add("badge");
        badge.getElement().getThemeList().add(theme(score));
        return badge;
    }

    public static String shortLabel(int score) {
        if (score >= 80) {
            return "Kritik";
        }
        if (score >= 40) {
            return "Orta";
        }
        return "Düşük";
    }

    public static String theme(int score) {
        if (score >= 80) {
            return "error";
        }
        if (score >= 40) {
            return "warning";
        }
        return "success";
    }

    public static String label(int score) {
        if (score >= 80) {
            return "KRİTİK ÖNCELİKLİ";
        }
        if (score >= 40) {
            return "ORTA ÖNCELİKLİ";
        }
        return "DÜŞÜK ÖNCELİKLİ";
    }

    public static String color(int score) {
        if (score >= 80) {
            return "#c62828";
        }
        if (score >= 40) {
            return "#e65100";
        }
        return "#2e7d32";
    }
}
