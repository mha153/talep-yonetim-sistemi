package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.AppNotification;
import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Data access for {@link AppNotification} rows. */
public interface NotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByRecipientOrderByCreatedAtDesc(AppUser recipient);

    long countByRecipientAndReadFalse(AppUser recipient);

    List<AppNotification> findByRequest(Request request);

    boolean existsByRecipient(AppUser recipient);

    boolean existsByActor(AppUser actor);
}
