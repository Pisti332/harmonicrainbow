package com.pisti.harmonicrainbow.controller;


import com.pisti.harmonicrainbow.model.Image;
import com.pisti.harmonicrainbow.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<Object> uploadImage(@RequestParam("image") MultipartFile file,
                                              @RequestParam("email") String email) throws IOException {
        return imageService.uploadImage(file, email);
    }
    @GetMapping("{email}")
    public ResponseEntity<Object> getImagesByEmail(@PathVariable String email) {
        Set<Image> images = imageService.getImagesByEmail(email);
        if (images == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(images, HttpStatus.OK);
        }
    }
    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Object> getImageByEmailAndName(@RequestParam String email, @RequestParam String name) {
        return imageService.getImageByEmailAndName(email, name);
    }


}
