package com.requestmanagement.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "MUSTAFA_PRIORITIZATIONS")
public class Prioritization {

    @Id
    @GeneratedValue
    @Column(name = "priority_id")
    private Long priorityId;

    @OneToOne
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private Request request;

    @Column(name = "urgency", nullable = false)
    private Integer urgency;

    @Column(name = "impact", nullable = false)
    private Integer impact;

    @Column(name = "priority_score", nullable = false)
    private Integer priorityScore;

    public Long getPriorityId() { return priorityId; }
    public void setPriorityId(Long priorityId) { this.priorityId = priorityId; }

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }

    public Integer getUrgency() { return urgency; }
    public void setUrgency(Integer urgency) { this.urgency = urgency; }

    public Integer getImpact() { return impact; }
    public void setImpact(Integer impact) { this.impact = impact; }

    public Integer getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Integer priorityScore) { this.priorityScore = priorityScore; }
}
