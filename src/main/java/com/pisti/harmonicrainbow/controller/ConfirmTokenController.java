package com.pisti.harmonicrainbow.controller;
import com.pisti.harmonicrainbow.service.user.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("api/user/confirmtoken")
@RequiredArgsConstructor
public class ConfirmTokenController {
    private final SignupService signupService;

    @GetMapping
    public String confirmEmail(@RequestParam String token) {
        Map<String, String> tokenCheckResponse = signupService.checkToken(token);
        if (tokenCheckResponse.get("validation successful").equals("true")) {
            return "confirmSuccessfulHtml";
        }
        else return "confirmFailedHtml";
    }
}
