package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.Prioritization;
import com.requestmanagement.base.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrioritizationRepository extends JpaRepository<Prioritization, Long> {
    Optional<Prioritization> findByRequest(Request request);
}
