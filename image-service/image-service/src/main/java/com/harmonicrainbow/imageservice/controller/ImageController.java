package com.harmonicrainbow.imageservice.controller;


import com.harmonicrainbow.imageservice.model.DTOs.PostImageDTO;
import com.harmonicrainbow.imageservice.model.ImageFormat;
import com.harmonicrainbow.imageservice.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/image")
public class ImageController {
    private ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<Object> postImage(@RequestBody PostImageDTO postImageDTO,
                                            HttpServletRequest request) {
        String token = request.getHeader("token");
        return imageService.postImage(postImageDTO, token);
    }
}
