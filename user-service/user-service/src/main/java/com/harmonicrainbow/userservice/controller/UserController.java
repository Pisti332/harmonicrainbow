package com.harmonicrainbow.userservice.controller;

import com.harmonicrainbow.userservice.model.SignupForm;
import com.harmonicrainbow.userservice.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final SignupService signupService;
    @Autowired
    public UserController(SignupService signupService) {
        this.signupService = signupService;
    }
    @PostMapping("signup")
    public Map<String, String> signupUser(@RequestBody SignupForm signupForm) {
        return signupService.registerUser(signupForm);
    }
}
