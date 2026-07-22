package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestActivity;
import com.requestmanagement.base.model.RequestAttachment;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Row-details panel (description + attachments + activity timeline) reused by every request/workflow grid. */
public class RequestDetailsPanel extends VerticalLayout {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    public RequestDetailsPanel(Request request, RequestActivityRepository activityRepository,
                                RequestAttachmentRepository attachmentRepository) {
        setPadding(false);
        setSpacing(false);

        Span title = new Span("Başlık: " + request.getTitle());
        title.getStyle().set("font-weight", "600");
        add(title);

        Span description = new Span("Açıklama: " + request.getDescription());
        description.getStyle().set("white-space", "pre-wrap");
        add(description);

        List<RequestAttachment> attachments = attachmentRepository.findByRequestOrderByCreatedAtAsc(request);
        if (!attachments.isEmpty()) {
            H4 attachmentsHeading = new H4("Ekler");
            attachmentsHeading.getStyle().set("margin-bottom", "0");
            add(attachmentsHeading);
            HorizontalLayout previews = new HorizontalLayout();
            attachments.forEach(attachment -> previews.add(AttachmentPreview.create(attachment)));
            add(previews);
        }

        List<RequestActivity> activities = activityRepository.findByRequestOrderByCreatedAtAsc(request);
        if (activities.isEmpty()) {
            return;
        }

        H4 heading = new H4("Geçmiş");
        heading.getStyle().set("margin-bottom", "0");
        add(heading);
        activities.forEach(activity -> add(new Span(
                TIMESTAMP_FORMAT.format(activity.getCreatedAt()) + " — " + activity.getDescription())));
    }
}
