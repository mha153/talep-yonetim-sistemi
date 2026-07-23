package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.EmailChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/** Data access for {@link EmailChangeRequest} rows. */
public interface EmailChangeRequestRepository extends JpaRepository<EmailChangeRequest, Long> {
    boolean existsByUser(AppUser user);
}
