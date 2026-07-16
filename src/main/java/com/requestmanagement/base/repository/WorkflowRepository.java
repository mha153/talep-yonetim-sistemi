package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByWorkflowStatusNot(WorkflowStatus workflowStatus);

    List<Workflow> findByWorkflowStatus(WorkflowStatus workflowStatus);

    boolean existsByRequest(Request request);

    Optional<Workflow> findByRequest(Request request);
}
