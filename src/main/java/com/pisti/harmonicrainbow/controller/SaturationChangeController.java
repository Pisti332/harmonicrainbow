package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.SaturationChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("api/service/saturation-change")
public class SaturationChangeController {
    private SaturationChangeService saturationChangeService;

    @Autowired
    public SaturationChangeController(SaturationChangeService saturationChangeService) {
        this.saturationChangeService = saturationChangeService;
    }
    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Object> changeSaturation(@RequestParam String email,
                                                   @RequestParam String name,
                                                   @RequestParam Integer saturation) {
        return saturationChangeService.changeSaturation(email, name, saturation);
    }
}
