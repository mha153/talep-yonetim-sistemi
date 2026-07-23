package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Data access for {@link Workflow} rows. */
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByWorkflowStatusNot(WorkflowStatus workflowStatus);

    List<Workflow> findByWorkflowStatus(WorkflowStatus workflowStatus);

    List<Workflow> findByDeveloperIsNullAndWorkflowStatusNot(WorkflowStatus workflowStatus);

    List<Workflow> findByDeveloperAndWorkflowStatusNot(AppUser developer, WorkflowStatus workflowStatus);

    List<Workflow> findByDeveloper(AppUser developer);

    boolean existsByRequest(Request request);

    boolean existsByDeveloper(AppUser developer);

    boolean existsByDeveloperAndWorkflowStatusNot(AppUser developer, WorkflowStatus workflowStatus);

    Optional<Workflow> findByRequest(Request request);
}
