package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.interfaces.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User, UUID> implements UserRepository {
    public UserRepositoryImpl(EntityManager em, ModelMapper modelMapper) {
        super(em, modelMapper, User.class);
    }

    @Override
    public Optional<User> findByEmailOrUsername(String email, String username) {
        String sql = """
                SELECT u
                FROM User u
                WHERE u.email = :email OR u.username = :username""";

        User user = em.createQuery(sql, User.class)
                .setParameter("email", email)
                .setParameter("username", username)
                .getSingleResult();

        if (user != null) {
            return Optional.of(user);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = """
                SELECT u
                FROM User u
                WHERE u.email = :email""";

        User user = em.createQuery(sql, User.class)
                .setParameter("email", email)
                .getSingleResult();

        if (user != null) {
            return Optional.of(user);
        }

        return Optional.empty();
    }
}
