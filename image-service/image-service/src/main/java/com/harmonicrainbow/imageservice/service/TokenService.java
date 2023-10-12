package com.harmonicrainbow.imageservice.service;

import com.harmonicrainbow.imageservice.model.Token;
import com.harmonicrainbow.imageservice.repository.TokenRepo;
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

    public ResponseEntity<Object> storeToken(Map<String, String> serviceRequest) {
        String serviceToken = serviceRequest.get("serviceToken");
        String token = serviceRequest.get("token");
        Map<String, String> response = new HashMap<>();
        if (!TokenService.SERVICE_TOKEN.equals(serviceToken)) {
            response.put("isAuthorizationAdded", "false");
            response.put("reason", "service token invalid");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        Token tokenEntity = Token.builder()
                .creationTime(LocalDateTime.now())
                .token(UUID.fromString(token)).
                uuid(UUID.randomUUID())
                .build();
        tokenRepo.save(tokenEntity);
        response.put("isAuthorizationAdded", "true");
        response.put("reason", "valid credentials");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Object> deleteToken(String token, String serviceToken) {
        Map<String, String> response = new HashMap<>();
        UUID tokenAsUUID = UUID.fromString(token);
        if (!serviceToken.equals(TokenService.SERVICE_TOKEN)) {
            response.put("isDeleteSuccessful", "false");
            response.put("reason", "invalid service token");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Token tokenFromDb = tokenRepo.findByToken(tokenAsUUID);

        if (tokenFromDb == null) {
            response.put("isDeleteSuccessful", "false");
            response.put("reason", "token doesn't exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        tokenRepo.deleteByToken(tokenAsUUID);
        response.put("isDeleteSuccessful", "true");
        response.put("reason", "valid credentials");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public boolean checkIfTokenExists(UUID token) {
        return tokenRepo.existsTokenByToken(token);
    }
}
