package com.requestmanagement.base.model;

/** The triage state of a {@link Request}, before it is converted into a {@link Workflow}. */
public enum RequestStatus {
    NEW,
    UNDER_REVIEW,
    PRIORITIZED,
    REJECTED;

    public String displayLabel() {
        return switch (this) {
            case NEW -> "Yeni";
            case UNDER_REVIEW -> "İncelemede";
            case PRIORITIZED -> "Önceliklendirildi";
            case REJECTED -> "Reddedildi";
        };
    }
}
