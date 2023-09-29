package com.harmonicrainbow.userservice.service;

import com.harmonicrainbow.userservice.model.DTOS.SignoutForm;
import com.harmonicrainbow.userservice.model.User;
import com.harmonicrainbow.userservice.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SignoutService {
    private UsersRepo usersRepo;

    @Autowired
    public SignoutService(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }
    public ResponseEntity<Object> signoutUser(SignoutForm signoutForm) {
        Map<String, String> response = new HashMap<>();
        try {
            String password = signoutForm.password();
            String email = signoutForm.email();
            String token = signoutForm.token();
            User user = usersRepo.findByEmailAndPassword(email, String.valueOf(password.hashCode()));
            user.setLoggedIn(false);
            usersRepo.save(user);

            String currentIp = System.getenv("IPV4");
            String url = "http://" + currentIp + ":8060/api/image/deletetoken";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> map = new HashMap<>();
            map.put("serviceToken", "b6e08c93-6a25-4e97-bb68-5bd58ff5f4ce");
            map.put("token", token);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(new HashMap<>(map));
            restTemplate.postForEntity(url, request, String.class);

            response.put("isLogoutSuccessful", "true");
            response.put("reason", "valid credentials");

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            response.put("isLogoutSuccessful", "false");
            response.put("reason", "invalid credentials or session doesn't exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }
}
