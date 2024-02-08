package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.ColorCompositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/analytics")
@RequiredArgsConstructor
public class ColorCompositionAnalyticsController {
    private final ColorCompositionService colorCompositionService;

    @GetMapping("color-composition")
    public ResponseEntity<Object> getColorComposition(@RequestParam String email, @RequestParam String name) {
        Map<String, Integer> colorComposition = colorCompositionService.getColorComposition(email, name);
        if (colorComposition == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(colorComposition, HttpStatus.OK);
        }
    }
}
