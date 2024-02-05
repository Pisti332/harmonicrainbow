package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.user.SigninService;
import com.pisti.harmonicrainbow.service.user.SignupService;
import com.pisti.harmonicrainbow.model.DTOS.SignupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    public ResponseEntity<Object> signupUser(@RequestBody SignupForm signupForm) {
        Map<String, String> signinObj = signupService.registerUser(signupForm);
        if (signinObj.get("isSignupSuccessful").equals("true")) {
            return new ResponseEntity<>(signinObj, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(signinObj, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("signin")
    public ResponseEntity<Object> signinUser(@RequestBody SignupForm signupForm) {
        Map<String, String> signinObj = signinService.signinUser(signupForm);
        if (signinObj.get("isLoginSuccessful").equals("true")) {
            MultiValueMap<String, String> headers = new HttpHeaders();
            headers.put("Authorization", Collections.singletonList("Bearer " + signinObj.get("auth")));
            return new ResponseEntity<>(signinObj, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(signinObj, HttpStatus.BAD_REQUEST);
        }
    }
}
