package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.SaturationChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("api/service/saturation-change")
@RequiredArgsConstructor
public class SaturationChangeController {
    private final SaturationChangeService saturationChangeService;

    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Object> changeSaturation(@RequestParam String userId,
                                                   @RequestParam String name,
                                                   @RequestParam Integer saturation) {
        ByteArrayResource byteArrayResource = saturationChangeService.changeSaturation(userId, name, saturation);
        if (byteArrayResource == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(byteArrayResource, HttpStatus.OK);
        }
    }
}
