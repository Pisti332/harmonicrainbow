package com.harmonicrainbow.imageservice.controller;

import com.harmonicrainbow.imageservice.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/image/addtoken")
public class TokenController {
    private TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    @PostMapping
    public ResponseEntity<Object> addToken(@RequestBody Map<String, String> serviceRequest) {
        return tokenService.storeToken(serviceRequest);
    }

}
