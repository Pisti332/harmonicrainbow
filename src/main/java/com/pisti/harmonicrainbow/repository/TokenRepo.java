package com.pisti.harmonicrainbow.repository;

import com.pisti.harmonicrainbow.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface TokenRepo extends JpaRepository<Token, UUID> {
    void deleteByToken(UUID token);
    Token findByToken(UUID uuid);
    boolean existsTokenByToken(UUID token);
}
