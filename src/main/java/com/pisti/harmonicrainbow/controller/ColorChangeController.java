package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.ColorChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/service")
@RequiredArgsConstructor
public class ColorChangeController {

    private final ColorChangeService colorChangeService;

    @PostMapping(path = "change-color", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Object> changeColor(@RequestBody Map<String, Map<String, Integer>> body,
                                              @RequestParam String email, @RequestParam String name) {
        ByteArrayResource byteArrayResource = colorChangeService.changeColors(body, email, name);
        return new ResponseEntity<>(byteArrayResource, HttpStatus.OK);
    }
}

