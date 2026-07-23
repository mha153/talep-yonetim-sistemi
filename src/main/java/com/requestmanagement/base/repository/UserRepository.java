package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Data access for {@link AppUser} rows. */
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    List<AppUser> findByRole(Role role);
}
