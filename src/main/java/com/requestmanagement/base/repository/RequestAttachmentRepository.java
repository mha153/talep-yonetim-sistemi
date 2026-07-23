package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Data access for {@link RequestAttachment} rows. */
public interface RequestAttachmentRepository extends JpaRepository<RequestAttachment, Long> {
    List<RequestAttachment> findByRequestOrderByCreatedAtAsc(Request request);
}
