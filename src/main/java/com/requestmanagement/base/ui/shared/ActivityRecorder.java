package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestActivity;
import com.requestmanagement.base.repository.RequestActivityRepository;

/** Appends a line to a request's activity timeline (shown in {@link RequestDetailsPanel}). */
public final class ActivityRecorder {

    private ActivityRecorder() {
    }

    public static void record(RequestActivityRepository activityRepository, Request request, String description) {
        RequestActivity activity = new RequestActivity();
        activity.setRequest(request);
        activity.setDescription(description);
        activityRepository.save(activity);
    }
}
