package com.pisti.harmonicrainbow.repository;

import com.pisti.harmonicrainbow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsersRepo extends JpaRepository<User, UUID> {
    User findByEmail(String email);
    List<User> findByEmailConfirmationToken(UUID token);
    User findFirstByEmail(String email);
}
