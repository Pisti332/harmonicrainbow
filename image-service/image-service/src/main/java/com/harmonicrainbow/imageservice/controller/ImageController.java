package com.harmonicrainbow.imageservice.controller;


import com.harmonicrainbow.imageservice.model.DTOs.PostImageDTO;
import com.harmonicrainbow.imageservice.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/image")
public class ImageController {
    private ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    @PostMapping
    public ResponseEntity<Object> uploadImage(@RequestParam("image") MultipartFile file, @RequestParam("email") String email,
                                              HttpServletRequest request) throws IOException {
        String token = request.getHeader("token");
        return imageService.uploadImage(file, email, token);
    }
    @GetMapping("{email}")
    public ResponseEntity<Object> getImagesByEmail(@PathVariable String email) {
        return imageService.getImagesByEmail(email);
    }

}
