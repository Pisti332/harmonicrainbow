package com.harmonicrainbow.imageservice.service;

import com.harmonicrainbow.imageservice.model.DTOs.PostImageDTO;
import com.harmonicrainbow.imageservice.model.Image;
import com.harmonicrainbow.imageservice.model.ImageFormat;
import com.harmonicrainbow.imageservice.repository.ImageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageService {
    private ImageRepo imageRepo;

    @Autowired
    public ImageService(ImageRepo imageRepo) {
        this.imageRepo = imageRepo;
    }

    public ResponseEntity<Object> postImage(PostImageDTO postImageDTO, String token) {
        Map<String, String> response = new HashMap<>();
        if (!token.equals(TokenService.SERVICE_TOKEN)) {
            response.put("isUploadSuccessful", "false");
            response.put("reason", "invalid service token");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        String imageData = postImageDTO.imageData();
        String imageName = postImageDTO.imageName();
        String imageFormat = postImageDTO.imageFormat();
        String email = postImageDTO.email();
        // TODO make 48px small image
        Image image = Image
                .builder()
                .name(imageName)
                .format(imageFormat)
                .email(email)
                .image48Px("test")
                .upload_time(LocalDateTime.now())
                .build();

        imageRepo.save(image);
        response.put("isUploadSuccessful", "true");
        response.put("reason", "valid credentials");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
