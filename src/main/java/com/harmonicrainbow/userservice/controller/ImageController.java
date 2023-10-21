package com.harmonicrainbow.userservice.controller;


import com.harmonicrainbow.userservice.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public ResponseEntity<Object> getImagesByEmail(@PathVariable String email, HttpServletRequest request) {
        String token = request.getHeader("token");
        return imageService.getImagesByEmail(email, token);
    }
    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Object> getImageByEmailAndName(@RequestParam String email, @RequestParam String name, HttpServletRequest request) {
        String token = request.getHeader("token");
        return imageService.getImageByEmailAndName(email, name, token);
    }


}
