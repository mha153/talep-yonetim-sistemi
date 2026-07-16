package com.talep.base.repository;

import com.talep.base.model.Prioritization;
import com.talep.base.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrioritizationRepository extends JpaRepository<Prioritization, Long> {
    Optional<Prioritization> findByRequest(Request request);
}
