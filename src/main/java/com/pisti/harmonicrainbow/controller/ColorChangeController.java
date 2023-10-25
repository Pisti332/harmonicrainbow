package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.ColorChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/service")
public class ColorChangeController {
    @Autowired
    public ColorChangeController(ColorChangeService colorChangeService) {
        this.colorChangeService = colorChangeService;
    }

    private ColorChangeService colorChangeService;
    @PostMapping(name = "change-color", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Object> changeColor(@RequestBody Map<String, Map<String, Integer>> body,
                                              @RequestParam String email, @RequestParam  String name) {
        return colorChangeService.changeColors(body, email, name);
    }
}

