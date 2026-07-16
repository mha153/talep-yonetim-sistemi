package com.requestmanagement.base.model;

public enum WorkflowStatus {
    BACKLOG,
    IN_PROGRESS,
    TESTING,
    DONE;

    public String displayLabel() {
        return switch (this) {
            case BACKLOG -> "Bekliyor";
            case IN_PROGRESS -> "Devam Ediyor";
            case TESTING -> "Test Ediliyor";
            case DONE -> "Tamamlandı";
        };
    }
}
