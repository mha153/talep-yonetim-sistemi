package com.requestmanagement.base.model;

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
