package com.pisti.harmonicrainbow.controller;


import com.pisti.harmonicrainbow.service.ImageService;
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
    public ResponseEntity<Object> uploadImage(@RequestParam("image") MultipartFile file, @RequestParam("email") String email) throws IOException {
        return imageService.uploadImage(file, email);
    }
    @GetMapping("{email}")
    public ResponseEntity<Object> getImagesByEmail(@PathVariable String email) {
        return imageService.getImagesByEmail(email);
    }
    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Object> getImageByEmailAndName(@RequestParam String email, @RequestParam String name) {
        return imageService.getImageByEmailAndName(email, name);
    }


}
