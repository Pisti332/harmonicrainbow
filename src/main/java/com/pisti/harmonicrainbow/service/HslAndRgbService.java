package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.service.utility.HslAndRgbConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HslAndRgbService {
    private final HslAndRgbConverter hslAndRgbConverter;

    public Map<String, Float> getHslFromRgb(int r, int g, int b) {
        return hslAndRgbConverter.convertRGBtoHSL(r, g, b);
    }

    public Map<String, Integer> getRgbFromHsl(int h, int s, int l) {
        return hslAndRgbConverter.convertHSLtoRGB(h, s, l);
    }
}
