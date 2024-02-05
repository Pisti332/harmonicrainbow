package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.BlackAndWhiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("api/service")
@RequiredArgsConstructor
public class BlackAndWhiteController {
    private final BlackAndWhiteService blackAndWhiteService;

    @PostMapping(path = "black-and-white", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<ByteArrayResource> getBlackAndWhite(@RequestParam String email, @RequestParam String name) {
        ByteArrayResource byteArrayResource = blackAndWhiteService.getBlackAndWhite(email, name);
        if (byteArrayResource == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(byteArrayResource.contentLength())
                .body(byteArrayResource);
    }
}
