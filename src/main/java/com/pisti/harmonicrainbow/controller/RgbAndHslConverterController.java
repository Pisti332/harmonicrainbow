package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.HslAndRgbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/service")
public class RgbAndHslConverterController {
    private final HslAndRgbService hslAndRgbService;

    @Autowired
    public RgbAndHslConverterController(HslAndRgbService hslAndRgbService) {
        this.hslAndRgbService = hslAndRgbService;
    }

    @GetMapping("rgbtohsl")
    public ResponseEntity<Object> getHslFromRgb(@RequestParam int r,
                                                @RequestParam int g,
                                                @RequestParam int b) {
        return hslAndRgbService.getHslFromRgb(r, g, b);
    }
    @GetMapping("hsltorgb")
    public ResponseEntity<Object> getRgbFromHsl(@RequestParam int h,
                                                @RequestParam int s,
                                                @RequestParam int l) {
        return hslAndRgbService.getRgbFromHsl(h, s, l);
    }
}
