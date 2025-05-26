package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailOrUsername(String email, String username);
}
