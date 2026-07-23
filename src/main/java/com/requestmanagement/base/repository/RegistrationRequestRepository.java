package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/** Data access for {@link RegistrationRequest} rows. */
public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {
    boolean existsByRequestedEmail(String requestedEmail);
}
