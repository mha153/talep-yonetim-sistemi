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

/** Named AppNotification (not Notification) to avoid colliding with Vaadin's own Notification class. */
@Entity
@Table(name = "MUSTAFA_NOTIFICATIONS")
public class AppNotification {

    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private AppUser recipient;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private AppUser actor;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public AppUser getRecipient() { return recipient; }
    public void setRecipient(AppUser recipient) { this.recipient = recipient; }

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }

    public AppUser getActor() { return actor; }
    public void setActor(AppUser actor) { this.actor = actor; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Instant getCreatedAt() { return createdAt; }
}
