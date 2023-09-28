package com.harmonicrainbow.userservice.controller;

import com.harmonicrainbow.userservice.service.SigninService;
import com.harmonicrainbow.userservice.service.SignupService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("api/user/confirmtoken")
public class ConfirmTokenController {
    SignupService signupService;
    @Autowired
    public ConfirmTokenController(SignupService signupService) {
        this.signupService = signupService;
    }

    @GetMapping
    public String confirmEmail(@RequestParam String token) {
        Map<String, String> tokenCheckResponse = signupService.checkToken(token);
        if (tokenCheckResponse.get("validation successful").equals("true")) {
            return "confirmSuccessfulHtml";
        }
        else return "confirmFailedHtml";
    }
}
