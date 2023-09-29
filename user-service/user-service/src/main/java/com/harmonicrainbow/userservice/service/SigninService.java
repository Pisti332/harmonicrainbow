package com.harmonicrainbow.userservice.service;

import com.harmonicrainbow.userservice.model.DTOS.SignupForm;
import com.harmonicrainbow.userservice.repository.UsersRepo;
import com.harmonicrainbow.userservice.service.utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.harmonicrainbow.userservice.model.User;

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
        UUID token = UUID.randomUUID();
        if (!sendTokenToImageService(token)) {
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

    private boolean sendTokenToImageService(UUID token) {
        try {
            String currentIp = System.getenv("IPV4");
            String url = "http://" + currentIp + ":8060/api/image/addtoken";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> map = new HashMap<>();
            map.put("serviceToken", "b6e08c93-6a25-4e97-bb68-5bd58ff5f4ce");
            map.put("token", token.toString());

            HttpEntity<Map<String, String>> request = new HttpEntity<>(new HashMap<>(map));
            restTemplate.postForEntity(url, request, String.class);
            return true;
        }
        catch (Exception e) {
            return false;
        }

    }
}
