package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.ColorCompositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/analytics")
public class ColorCompositionAnalyticsController {
    private ColorCompositionService colorCompositionService;

    @Autowired
    public ColorCompositionAnalyticsController(ColorCompositionService colorCompositionService) {
        this.colorCompositionService = colorCompositionService;
    }

    @GetMapping("color-composition")
    public ResponseEntity<Object> getColorComposition(@RequestParam String email, @RequestParam String name) {
        return colorCompositionService.getColorComposition(email, name);
    }
}
