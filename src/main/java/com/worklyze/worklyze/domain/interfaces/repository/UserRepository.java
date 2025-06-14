package com.worklyze.worklyze.domain.interfaces.repository;

import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.User;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseRepository<User, UUID> {
    Optional<User> findByEmailOrUsername(String email, String username);
    Optional<User> findByEmail(String email);

}
