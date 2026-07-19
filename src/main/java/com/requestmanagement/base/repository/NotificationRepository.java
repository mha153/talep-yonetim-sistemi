package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.AppNotification;
import com.requestmanagement.base.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByRecipientOrderByCreatedAtDesc(AppUser recipient);

    long countByRecipientAndReadFalse(AppUser recipient);
}
