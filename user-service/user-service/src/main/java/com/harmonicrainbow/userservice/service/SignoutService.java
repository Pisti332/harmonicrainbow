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

            String domain = System.getenv("IPV4");
            String url = "http://" + domain + ":8060/api/image/deletetoken?" +
                    "serviceToken=" + "b6e08c93-6a25-4e97-bb68-5bd58ff5f4ce&token=" + token;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>("body", headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

            String body = responseEntity.getBody();

            System.out.println(body);

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
