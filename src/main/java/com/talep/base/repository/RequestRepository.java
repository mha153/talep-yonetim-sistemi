package com.talep.base.repository;

import com.talep.base.model.Request;
import com.talep.base.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStatusIn(Collection<RequestStatus> statuses);

    List<Request> findByCustomer_Email(String email);
}
