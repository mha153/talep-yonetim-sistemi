package com.requestmanagement.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/** A visitor's self-service sign-up request; only becomes an {@link AppUser} once the PO approves it. */
@Entity
@Table(name = "MUSTAFA_REGISTRATIONS")
public class RegistrationRequest {

    @Id
    @GeneratedValue
    @Column(name = "registration_id")
    private Long registrationId;

    @Column(name = "requested_email", nullable = false, unique = true, length = 100)
    private String requestedEmail;

    @Column(name = "name_surname", nullable = false, length = 100)
    private String nameSurname;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public String getRequestedEmail() { return requestedEmail; }
    public void setRequestedEmail(String requestedEmail) { this.requestedEmail = requestedEmail; }

    public String getNameSurname() { return nameSurname; }
    public void setNameSurname(String nameSurname) { this.nameSurname = nameSurname; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Instant getCreatedAt() { return createdAt; }
}
