package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.RequestAttachment;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;

import java.io.ByteArrayInputStream;

/** Renders one {@link RequestAttachment} as a clickable thumbnail (images) or a download link (other files). */
final class AttachmentPreview {

    private AttachmentPreview() {
    }

    static Component create(RequestAttachment attachment) {
        if (attachment.getContentType() != null && attachment.getContentType().startsWith("image/")) {
            Image thumbnail = new Image(attachment.getData(), attachment.getFileName(), attachment.getContentType());
            thumbnail.setMaxWidth("160px");
            thumbnail.setMaxHeight("160px");
            thumbnail.getStyle().set("cursor", "pointer");
            thumbnail.addClickListener(e -> openPreviewDialog(attachment));
            return thumbnail;
        }

        DownloadHandler handler = DownloadHandler.fromInputStream(event ->
                new DownloadResponse(new ByteArrayInputStream(attachment.getData()), attachment.getFileName(),
                        attachment.getContentType(), attachment.getData().length));
        Anchor link = new Anchor(handler, attachment.getFileName());
        link.setTarget("_blank");
        return link;
    }

    private static void openPreviewDialog(RequestAttachment attachment) {
        Image fullSize = new Image(attachment.getData(), attachment.getFileName(), attachment.getContentType());
        fullSize.setMaxWidth("80vw");
        fullSize.setMaxHeight("80vh");

        Dialog dialog = new Dialog(fullSize);
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.open();
    }
}
