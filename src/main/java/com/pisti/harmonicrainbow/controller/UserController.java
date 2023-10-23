package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.user.SigninService;
import com.pisti.harmonicrainbow.service.user.SignupService;
import com.pisti.harmonicrainbow.model.DTOS.SignupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> signupUser(@RequestBody SignupForm signupForm) {
        return signupService.registerUser(signupForm);
    }
    @PostMapping("signin")
    public ResponseEntity<Object> signinUser(@RequestBody SignupForm signupForm) {
        return signinService.signinUser(signupForm);
    }
}
