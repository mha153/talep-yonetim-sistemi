package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestActivity;
import com.requestmanagement.base.repository.RequestActivityRepository;

/** Appends a line to a request's activity timeline (shown in {@link RequestDetailsPanel}). */
final class ActivityRecorder {

    private ActivityRecorder() {
    }

    static void record(RequestActivityRepository activityRepository, Request request, String description) {
        RequestActivity activity = new RequestActivity();
        activity.setRequest(request);
        activity.setDescription(description);
        activityRepository.save(activity);
    }
}
