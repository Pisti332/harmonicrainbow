package com.harmonicrainbow.userservice.controller;

import com.harmonicrainbow.userservice.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    @GetMapping("brightness")
    public ResponseEntity<Object> getImageBrightness(@RequestParam String email, @RequestParam String name,
                                                     HttpServletRequest request) {
        String token = request.getHeader("token");
        return analyticsService.getBrightness(email, name, token);
    }

}
