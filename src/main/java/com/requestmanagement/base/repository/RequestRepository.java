package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/** Data access for {@link Request} rows. */
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStatusIn(Collection<RequestStatus> statuses);

    List<Request> findByStatus(RequestStatus status);

    List<Request> findByCustomer_Email(String email);

    boolean existsByCustomer(AppUser customer);
}
