package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.BrightnessAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("api/analytics")
@RequiredArgsConstructor
public class BrightnessAnalyticsController {
    private final BrightnessAnalyticsService analyticsService;
    @GetMapping("brightness")
    public ResponseEntity<Object> getImageBrightness(@RequestParam String email, @RequestParam String name) {
        Map<String, Integer> data = analyticsService.getBrightness(email, name);
        if (data != null) {
            return new ResponseEntity<>(data, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
