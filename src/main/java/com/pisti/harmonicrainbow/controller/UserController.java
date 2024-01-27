package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.user.SigninService;
import com.pisti.harmonicrainbow.service.user.SignupService;
import com.pisti.harmonicrainbow.model.DTOS.SignupForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final SignupService signupService;
    private final SigninService signinService;

    @PostMapping("signup")
    public ResponseEntity<Object> signupUser(@RequestBody SignupForm signupForm) {
        Map<String, String> registerObj = signupService.registerUser(signupForm);
        if (registerObj.get("isSignupSuccessful").equals("false")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(registerObj, HttpStatus.CREATED);
        }
    }
    @PostMapping("signin")
    public ResponseEntity<Object> signinUser(@RequestBody SignupForm signupForm) {
        Map<String, String> loginObj = signinService.signinUser(signupForm);
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.put("Authorization", Collections.singletonList("Bearer " + loginObj.get("auth")));
        if (loginObj.get("isLoginSuccessful").equals("false")) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        else {
            return new ResponseEntity<>(loginObj, headers, HttpStatus.OK);
        }
    }
}
