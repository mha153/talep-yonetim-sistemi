package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStatusIn(Collection<RequestStatus> statuses);

    List<Request> findByStatus(RequestStatus status);

    List<Request> findByCustomer_Email(String email);
}
