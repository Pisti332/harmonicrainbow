package com.harmonicrainbow.userservice.service;

import com.harmonicrainbow.userservice.model.DTOS.SignupForm;
import com.harmonicrainbow.userservice.model.Token;
import com.harmonicrainbow.userservice.repository.TokenRepo;
import com.harmonicrainbow.userservice.repository.UsersRepo;
import com.harmonicrainbow.userservice.service.utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.harmonicrainbow.userservice.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SigninService {
    private UsersRepo usersRepo;
    private TokenService tokenService;

    @Autowired
    public SigninService(UsersRepo usersRepo, TokenService tokenService) {
        this.usersRepo = usersRepo;
        this.tokenService = tokenService;
    }

    public ResponseEntity<Object> signinUser(SignupForm signupForm) {
        Map<String, String> response = new HashMap<>();
        response.put("isLoginSuccessful", "false");
        response.put("token", "");
        if (!Validator.validateEmail(signupForm.email())) {
            response.put("reason", "wrong email format");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        User user = usersRepo.findByEmailAndPassword(signupForm.email(), String.valueOf(signupForm.password().hashCode()));
        if (user == null) {
            response.put("reason", "no user with this email and password combination");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (!user.isActive()) {
            response.put("reason", "email hasn't been confirmed yet");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (user.isLoggedIn()) {
            response.put("reason", "already logged in");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        UUID token = UUID.randomUUID();
        if (!tokenService.storeToken(token)) {
            response.put("reason", "service error, please try again later");
            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
        user.setLoggedIn(true);
        usersRepo.save(user);
        response.put("isLoginSuccessful", "true");
        response.put("token", token.toString());
        response.put("reason", "valid credentials");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
