package com.pisti.harmonicrainbow.service.user;

import com.pisti.harmonicrainbow.model.DTOS.SignupForm;
import com.pisti.harmonicrainbow.security.JWTService;
import com.pisti.harmonicrainbow.security.MyUserDetailsService;
import com.pisti.harmonicrainbow.service.utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SigninService {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public SigninService(AuthenticationManager authenticationManager,
                         JWTService jwtService, PasswordEncoder passwordEncoder, MyUserDetailsService myUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.myUserDetailsService = myUserDetailsService;
    }

    public Map<String, String> signinUser(SignupForm signupForm) {
        Map<String, String> response = new HashMap<>();
        response.put("isLoginSuccessful", "false");
        if (!Validator.validateEmail(signupForm.email())) {
            response.put("reason", "wrong email format");
            return response;
        }

        response.put("reason", "no user with this email and password combination");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signupForm.email(), signupForm.password())
            );
            UserDetails user = myUserDetailsService.loadUserByUsername(authentication.getName());


            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .builder()
                    .password(user.getPassword())
                    .username(user.getUsername())
                    .authorities(user.getAuthorities())
                    .build();
            String token = jwtService.generateToken(userDetails);
            if (!myUserDetailsService.isUserActive(authentication.getName())) {
                response.put("reason", "email hasn't been confirmed yet");
                return response;
            }
            response.put("isLoginSuccessful", "true");
            response.put("reason", "valid credentials");
            response.put("auth", token);
            return response;
        }
        catch (Exception e) {
            return response;
        }
    }
}
