package com.harmonicrainbow.userservice.controller;

import com.harmonicrainbow.userservice.model.DTOS.SignupForm;
import com.harmonicrainbow.userservice.service.SigninService;
import com.harmonicrainbow.userservice.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final SignupService signupService;
    private final SigninService signinService;
    @Autowired
    public UserController(SignupService signupService, SigninService signinService) {
        this.signupService = signupService;
        this.signinService = signinService;
    }
    @PostMapping("signup")
    public Map<String, String> signupUser(@RequestBody SignupForm signupForm) {
        return signupService.registerUser(signupForm);
    }
    @GetMapping("confirm")
    public Map<String, String> confirmEmail(@RequestParam String token) {
        return signupService.checkToken(token);
    }
    @PostMapping("signin")
    public Map<String, String> signinUser(@RequestBody SignupForm signupForm) {
        return signinService.signinUser(signupForm);
    }
}
