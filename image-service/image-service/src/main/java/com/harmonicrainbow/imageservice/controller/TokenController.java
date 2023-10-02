package com.harmonicrainbow.imageservice.controller;

import com.harmonicrainbow.imageservice.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/image")
// TODO change endpoint names to be restful
public class TokenController {
    private TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    @PostMapping("addtoken")
    public ResponseEntity<Object> addToken(@RequestBody Map<String, String> serviceRequest) {
        return tokenService.storeToken(serviceRequest);
    }
    @DeleteMapping("deletetoken")
    public ResponseEntity<Object> deleteToken(@RequestParam String token, @RequestParam String serviceToken) {
        return tokenService.deleteToken(token, serviceToken);
    }

}
