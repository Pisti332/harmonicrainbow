package com.harmonicrainbow.userservice.service.user;

import com.harmonicrainbow.userservice.model.Token;
import com.harmonicrainbow.userservice.repository.TokenRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {
    public static final String SERVICE_TOKEN = "b6e08c93-6a25-4e97-bb68-5bd58ff5f4ce";
    private TokenRepo tokenRepo;
    @Autowired
    public TokenService(TokenRepo tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    public boolean storeToken(UUID token) {
        try {
            Token tokenEntity = Token.builder()
                    .creationTime(LocalDateTime.now())
                    .token(token)
                    .uuid(UUID.randomUUID())
                    .build();
            tokenRepo.save(tokenEntity);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Map<String, String> deleteToken(UUID token) {
        Map<String, String> response = new HashMap<>();

        Token tokenFromDb = tokenRepo.findByToken(token);

        if (tokenFromDb == null) {
            response.put("isDeleteSuccessful", "false");
            response.put("reason", "token doesn't exist");
            return response;
        }
        tokenRepo.deleteByToken(token);
        response.put("isDeleteSuccessful", "true");
        response.put("reason", "valid credentials");
        return response;
    }
    public boolean checkIfTokenExists(UUID token) {
        return tokenRepo.existsTokenByToken(token);
    }
}
