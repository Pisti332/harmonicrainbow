package com.harmonicrainbow.userservice.controller;

import com.harmonicrainbow.userservice.model.DTOS.SignupForm;
import com.harmonicrainbow.userservice.service.SignupService;
import jakarta.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("confirm")
    public Map<String, String> confirmEmail(@RequestParam String token) {
        return signupService.checkToken(token);
    }
}
