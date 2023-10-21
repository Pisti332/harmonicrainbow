package com.harmonicrainbow.userservice.controller;

import com.harmonicrainbow.userservice.model.DTOS.SignoutForm;
import com.harmonicrainbow.userservice.model.DTOS.SignupForm;
import com.harmonicrainbow.userservice.service.SigninService;
import com.harmonicrainbow.userservice.service.SignoutService;
import com.harmonicrainbow.userservice.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final SignupService signupService;
    private final SigninService signinService;
    private final SignoutService signoutService;

    @Autowired
    public UserController(SignupService signupService, SigninService signinService, SignoutService signoutService) {
        this.signupService = signupService;
        this.signinService = signinService;
        this.signoutService= signoutService;
    }
    @PostMapping("signup")
    public ResponseEntity<Object> signupUser(@RequestBody SignupForm signupForm) {
        return signupService.registerUser(signupForm);
    }
    @PostMapping("signin")
    public ResponseEntity<Object> signinUser(@RequestBody SignupForm signupForm) {
        return signinService.signinUser(signupForm);
    }
    @PostMapping("signout")
    public ResponseEntity<Object> signoutUser(@RequestBody SignoutForm signoutForm, HttpServletRequest request) {
        return signoutService.signoutUser(signoutForm, request);
    }
}
