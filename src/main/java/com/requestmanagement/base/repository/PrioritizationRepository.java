package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.Prioritization;
import com.requestmanagement.base.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrioritizationRepository extends JpaRepository<Prioritization, Long> {
    Optional<Prioritization> findByRequest(Request request);
}
