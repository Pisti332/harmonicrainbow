package com.harmonicrainbow.userservice.service;

import com.harmonicrainbow.userservice.model.DTOS.SignupForm;
import com.harmonicrainbow.userservice.repository.UsersRepo;
import com.harmonicrainbow.userservice.service.utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.harmonicrainbow.userservice.model.User;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SigninService {
    private UsersRepo usersRepo;

    @Autowired
    public SigninService(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
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
        user.setLoggedIn(true);
        usersRepo.save(user);
        UUID token = UUID.randomUUID();
        response.put("isLoginSuccessful", "true");
        response.put("token", token.toString());
        response.put("reason", "valid credentials");
        //TODO send token to services as well
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
