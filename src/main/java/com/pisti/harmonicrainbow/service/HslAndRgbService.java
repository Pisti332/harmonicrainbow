package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.service.utility.HslAndRgbConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HslAndRgbService {
    private final HslAndRgbConverter hslAndRgbConverter;

    public ResponseEntity<Object> getHslFromRgb(int r, int g, int b) {
        Map<String, Float> values = hslAndRgbConverter.convertRGBtoHSL(r, g, b);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(values);
    }

    public ResponseEntity<Object> getRgbFromHsl(int h, int s, int l) {
        Map<String, Integer> values = hslAndRgbConverter.convertHSLtoRGB(h, s, l);
        return ResponseEntity.status(HttpStatus.OK).body(values);
    }
}
