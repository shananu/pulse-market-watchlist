package com.pulse.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserCheckpointRepository extends JpaRepository<UserCheckpoint, Long> {
    Optional<UserCheckpoint> findByUserId(Long userId);
}