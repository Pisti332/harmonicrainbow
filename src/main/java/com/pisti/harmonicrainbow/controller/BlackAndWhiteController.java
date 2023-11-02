package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.BlackAndWhiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@RequestMapping("api/service")
public class BlackAndWhiteController {
    private final BlackAndWhiteService blackAndWhiteService;

    @Autowired
    public BlackAndWhiteController(BlackAndWhiteService blackAndWhiteService) {
        this.blackAndWhiteService = blackAndWhiteService;
    }
    @PostMapping(path = "black-and-white", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Object> getBlackAndWhite(@RequestParam String email, @RequestParam String name) {
        return blackAndWhiteService.getBlackAndWhite(email, name);
    }

}
