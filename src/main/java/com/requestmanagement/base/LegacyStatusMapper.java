package com.requestmanagement.base;

import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.model.WorkflowStatus;
import org.jspecify.annotations.Nullable;

/** Maps legacy Turkish status strings from MUSTAFA_REQUESTS_LEGACY to the new enums. */
final class LegacyStatusMapper {

    private LegacyStatusMapper() {
    }

    static RequestStatus toRequestStatus(@Nullable String status) {
        if (status == null) {
            return RequestStatus.NEW;
        }
        return switch (status.trim()) {
            case "REDDEDİLDİ", "İPTAL EDİLDİ" -> RequestStatus.REJECTED;
            case "ÖNCELİKLENDİRİLDİ", "YAZILIMCIYA ATANDI", "SPRINT", "TAMAMLANDI" -> RequestStatus.PRIORITIZED;
            default -> RequestStatus.NEW;
        };
    }

    static @Nullable WorkflowStatus toWorkflowStatus(@Nullable String status) {
        if (status == null) {
            return null;
        }
        return switch (status.trim()) {
            case "YAZILIMCIYA ATANDI" -> WorkflowStatus.BACKLOG;
            case "SPRINT" -> WorkflowStatus.IN_PROGRESS;
            case "TAMAMLANDI" -> WorkflowStatus.DONE;
            default -> null;
        };
    }

    static String slugify(String name) {
        return name.trim().toLowerCase()
                .replace("ı", "i").replace("ş", "s").replace("ğ", "g")
                .replace("ü", "u").replace("ö", "o").replace("ç", "c")
                .replaceAll("[^a-z0-9]+", ".");
    }
}
