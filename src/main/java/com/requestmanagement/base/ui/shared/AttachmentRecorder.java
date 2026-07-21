package com.requestmanagement.base.ui.shared;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestAttachment;
import com.requestmanagement.base.repository.RequestAttachmentRepository;

/** Saves an uploaded file's bytes as a {@link RequestAttachment}. */
public final class AttachmentRecorder {

    private AttachmentRecorder() {
    }

    public static void record(RequestAttachmentRepository attachmentRepository, Request request, String fileName,
                               String contentType, byte[] data) {
        RequestAttachment attachment = new RequestAttachment();
        attachment.setRequest(request);
        attachment.setFileName(fileName);
        attachment.setContentType(contentType);
        attachment.setData(data);
        attachmentRepository.save(attachment);
    }
}
