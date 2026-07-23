package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.Prioritization;
import com.vaadin.flow.component.html.Span;

import java.time.Duration;
import java.time.Instant;

/** Shared score formula/theme/label mapping used by the pending-requests grid and prioritization dialog. */
public final class RequestScoreBadge {

    private RequestScoreBadge() {
    }

    /** Default effort (easiest) assumed until a developer provides their own estimate. */
    public static final int DEFAULT_EFFORT = 1;

    private static final int MAX_SCORE = 100;

    /** +3%/day compounding: keeps old, un-prioritized work climbing without saturating the 0-100 scale in days. */
    private static final double DAILY_AGING_RATE = 1.03;

    /** WSJF-style base score: business value (impact + urgency) divided by how hard the work is, scaled to 0-100. */
    public static int compute(int impact, int urgency, int effort) {
        return (int) Math.round((impact + urgency) / (double) effort * 10);
    }

    /**
     * The base score aged by how long the request has been waiting, so stale work naturally climbs in priority.
     * Recalculated live (not stored) so it stays current every time it's displayed.
     */
    public static int agedScore(Prioritization prioritization) {
        int base = prioritization.getPriorityScore();
        Instant createdAt = prioritization.getRequest().getCreatedAt();
        if (createdAt == null) {
            return base;
        }
        long daysWaited = Duration.between(createdAt, Instant.now()).toDays();
        double aged = base * Math.pow(DAILY_AGING_RATE, daysWaited);
        return (int) Math.min(MAX_SCORE, Math.round(aged));
    }

    public static Span create(Prioritization prioritization) {
        Span badge = new Span();
        if (prioritization == null) {
            badge.setText("Belirlenmedi");
            return badge;
        }
        int score = agedScore(prioritization);
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
