package com.requestmanagement.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/** One chat message on a {@link Request}, scoped to a {@link MessageChannel} (customer chat or internal note). */
@Entity
@Table(name = "MUSTAFA_REQUEST_MESSAGES")
public class RequestMessage {

    @Id
    @GeneratedValue
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private MessageChannel channel;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "is_read", nullable = false, columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private boolean read;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }

    public AppUser getAuthor() { return author; }
    public void setAuthor(AppUser author) { this.author = author; }

    public MessageChannel getChannel() { return channel; }
    public void setChannel(MessageChannel channel) { this.channel = channel; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Instant getCreatedAt() { return createdAt; }
}
