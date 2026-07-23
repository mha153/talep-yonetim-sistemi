package com.requestmanagement.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/** A user's self-service request to change their login email/username, pending PO approval. */
@Entity
@Table(name = "MUSTAFA_EMAIL_CHANGES")
public class EmailChangeRequest {

    @Id
    @GeneratedValue
    @Column(name = "change_id")
    private Long changeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "requested_email", nullable = false, length = 100)
    private String requestedEmail;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public Long getChangeId() { return changeId; }
    public void setChangeId(Long changeId) { this.changeId = changeId; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    public String getRequestedEmail() { return requestedEmail; }
    public void setRequestedEmail(String requestedEmail) { this.requestedEmail = requestedEmail; }

    public Instant getCreatedAt() { return createdAt; }
}
