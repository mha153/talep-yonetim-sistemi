package com.talep.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "MUSTAFA_WORKFLOWS")
public class Workflow {

    @Id
    @GeneratedValue
    @Column(name = "task_id")
    private Long taskId;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne
    @JoinColumn(name = "developer_id")
    private AppUser developer;

    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_status", nullable = false, length = 30)
    private WorkflowStatus workflowStatus = WorkflowStatus.BACKLOG;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }

    public AppUser getDeveloper() { return developer; }
    public void setDeveloper(AppUser developer) { this.developer = developer; }

    public WorkflowStatus getWorkflowStatus() { return workflowStatus; }
    public void setWorkflowStatus(WorkflowStatus workflowStatus) { this.workflowStatus = workflowStatus; }
}
