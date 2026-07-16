package com.talep.base.repository;

import com.talep.base.model.Request;
import com.talep.base.model.Workflow;
import com.talep.base.model.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByWorkflowStatusNot(WorkflowStatus workflowStatus);

    List<Workflow> findByWorkflowStatus(WorkflowStatus workflowStatus);

    boolean existsByRequest(Request request);
}
