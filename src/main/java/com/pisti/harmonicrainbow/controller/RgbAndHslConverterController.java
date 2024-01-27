package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.HslAndRgbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/service")
@RequiredArgsConstructor
public class RgbAndHslConverterController {
    private final HslAndRgbService hslAndRgbService;

    @GetMapping("rgbtohsl")
    public ResponseEntity<Object> getHslFromRgb(@RequestParam int r,
                                                @RequestParam int g,
                                                @RequestParam int b) {
        Map<String, Float> hsl = hslAndRgbService.getHslFromRgb(r, g, b);
        if (hsl == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(hsl, HttpStatus.OK);
        }
    }
    @GetMapping("hsltorgb")
    public ResponseEntity<Object> getRgbFromHsl(@RequestParam int h,
                                                @RequestParam int s,
                                                @RequestParam int l) {
        Map<String, Integer> rgb = hslAndRgbService.getRgbFromHsl(h, s, l);
        if (rgb == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(rgb, HttpStatus.OK);
        }
    }
}
