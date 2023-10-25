package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.BrightnessAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/analytics")
public class BrightnessAnalyticsController {
    private final BrightnessAnalyticsService analyticsService;

    @Autowired
    public BrightnessAnalyticsController(BrightnessAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    @GetMapping("brightness")
    public ResponseEntity<Object> getImageBrightness(@RequestParam String email, @RequestParam String name) {
        return analyticsService.getBrightness(email, name);
    }

}
