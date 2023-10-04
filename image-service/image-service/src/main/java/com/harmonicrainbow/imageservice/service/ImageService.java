package com.harmonicrainbow.imageservice.service;

import com.harmonicrainbow.imageservice.model.Image;
import com.harmonicrainbow.imageservice.repository.ImageRepo;
import com.harmonicrainbow.imageservice.service.utility.ImageNameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageService {
    private static final String UPLOAD_DIRECTORY = "image-service\\image-service\\src\\main\\resources\\images\\";
    private ImageRepo imageRepo;
    private ImageNameConverter imageNameConverter;

    @Autowired
    public ImageService(ImageRepo imageRepo, ImageNameConverter imageNameConverter) {
        this.imageRepo = imageRepo;
        this.imageNameConverter = imageNameConverter;
    }
    public ResponseEntity<Object> uploadImage(MultipartFile file, String email, String token) throws IOException {
        Map<String, String> response = new HashMap<>();
        if (!TokenService.SERVICE_TOKEN.equals(token)) {
            response.put("isUploadSuccessful", "false");
            response.put("reason", "invalid service token");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // TODO make 48px small image

        String imageName = file.getOriginalFilename();
        Map<String, String> imageMetadata = imageNameConverter.convertImageNameToNameAndFormat(imageName);

        Image image = Image
                .builder()
                .name(imageMetadata.get("name"))
                .format(imageMetadata.get("format"))
                .email(email)
                .image48Px("test")
                .upload_time(LocalDateTime.now())
                .build();

        imageRepo.save(image);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
        response.put("isSuccessful", "true");
        response.put("reason", "valid credentials");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
