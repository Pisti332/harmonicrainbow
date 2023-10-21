package com.harmonicrainbow.userservice.service.user;

import com.harmonicrainbow.userservice.model.DTOS.SignoutForm;
import com.harmonicrainbow.userservice.model.User;
import com.harmonicrainbow.userservice.repository.UsersRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SignoutService {
    private UsersRepo usersRepo;
    private TokenService tokenService;

    @Autowired
    public SignoutService(UsersRepo usersRepo, TokenService tokenService) {
        this.usersRepo = usersRepo;
        this.tokenService= tokenService;
    }

    @Transactional
    public ResponseEntity<Object> signoutUser(SignoutForm signoutForm, HttpServletRequest request) {
        UUID token = UUID.fromString(request.getHeader("token"));
        Map<String, String> response = new HashMap<>();
        String password = signoutForm.password();
        String email = signoutForm.email();

        User user = usersRepo.findByEmailAndPassword(email, String.valueOf(password.hashCode()));

        if (user == null) {
            return  new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Map<String, String> tokenDeleteResponse = tokenService.deleteToken(token);

        if (tokenDeleteResponse.get("isDeleteSuccessful").equals("false")) {
            return new ResponseEntity<>(tokenDeleteResponse, HttpStatus.BAD_REQUEST);
        }
        user.setLoggedIn(false);
        usersRepo.save(user);

        response.put("isLogoutSuccessful", "true");
        response.put("reason", "valid credentials");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
