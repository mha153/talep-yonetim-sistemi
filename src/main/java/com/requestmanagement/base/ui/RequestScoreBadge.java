package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.Prioritization;
import com.vaadin.flow.component.html.Span;

/** Shared score→theme/label mapping used by the pending-requests grid and prioritization dialog. */
final class RequestScoreBadge {

    private RequestScoreBadge() {
    }

    static Span create(Prioritization prioritization) {
        Span badge = new Span();
        if (prioritization == null) {
            badge.setText("Belirlenmedi");
            return badge;
        }
        int score = prioritization.getPriorityScore();
        badge.setText(String.valueOf(score));
        badge.getElement().getThemeList().add("badge");
        badge.getElement().getThemeList().add(theme(score));
        return badge;
    }

    static String theme(int score) {
        if (score >= 20) {
            return "error";
        }
        if (score >= 10) {
            return "warning";
        }
        return "success";
    }

    static String label(int score) {
        if (score >= 20) {
            return "KRİTİK ÖNCELİKLİ";
        }
        if (score >= 10) {
            return "ORTA ÖNCELİKLİ";
        }
        return "DÜŞÜK ÖNCELİKLİ";
    }
}
