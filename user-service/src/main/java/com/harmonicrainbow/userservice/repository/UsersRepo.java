package com.harmonicrainbow.userservice.repository;

import com.harmonicrainbow.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsersRepo extends JpaRepository<User, UUID> {
    List<User> findByEmail(String email);
    List<User> findByEmailConfirmationToken(UUID token);
    User findByEmailAndPassword(String email, String password);
    void deleteByEmailAndPassword(String email, String password);
}