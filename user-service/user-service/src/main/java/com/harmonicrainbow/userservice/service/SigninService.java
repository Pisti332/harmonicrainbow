package com.harmonicrainbow.userservice.service;

import com.harmonicrainbow.userservice.model.DTOS.SignupForm;
import com.harmonicrainbow.userservice.repository.UsersRepo;
import com.harmonicrainbow.userservice.service.utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Map<String, String> signinUser(SignupForm signupForm) {
        Map<String, String> response = new HashMap<>();
        response.put("isLoginSuccessful", "false");
        response.put("token", "");
        if (!Validator.validateEmail(signupForm.email())) {
            return response;
        }
        User user = usersRepo.findByEmailAndPassword(signupForm.email(), String.valueOf(signupForm.password().hashCode()));
        if (user == null) {
            return response;
        }
        UUID token = UUID.randomUUID();
        response.put("isLoginSuccessful", "true");
        response.put("token", token.toString());
        //TODO send token to services as well
        return response;
    }
}
