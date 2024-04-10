package com.pisti.harmonicrainbow.controller;


import com.pisti.harmonicrainbow.model.Image;
import com.pisti.harmonicrainbow.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<Object> uploadImage(@RequestParam("image") MultipartFile file,
                                              @RequestParam("email") String email) throws IOException {

        Map<String, String> response = imageService.uploadImage(file, email);
        if (response.get("isSuccessful").equals("true")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Object> getImageByEmailAndUserId(@RequestParam String userId, @RequestParam String name) {
        ByteArrayResource byteArrayResource = imageService.getImageByUserIdAndName(userId, name);
        if (byteArrayResource == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else return new ResponseEntity<>(byteArrayResource, HttpStatus.OK);
    }
    @DeleteMapping
    public ResponseEntity<Object> deleteByNameAndUserId(@RequestParam String userId, @RequestParam String name) {
        return imageService.deleteImageByNameAndUserId(userId, name);
    }
    @GetMapping("/admin")
    public ResponseEntity<Object> getAllImages() {
        List<Image> images = imageService.getAllImages();
        return new ResponseEntity<>(images, HttpStatus.OK);
    }
}
