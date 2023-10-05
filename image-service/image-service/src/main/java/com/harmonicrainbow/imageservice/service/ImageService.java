package com.harmonicrainbow.imageservice.service;

import com.harmonicrainbow.imageservice.model.Image;
import com.harmonicrainbow.imageservice.repository.ImageRepo;
import com.harmonicrainbow.imageservice.service.utility.ImageNameConverter;
import com.harmonicrainbow.imageservice.service.utility.ImageResizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zipkin2.Call;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ImageService {
    private static final String UPLOAD_DIRECTORY = "image-service\\image-service\\src\\main\\resources\\images\\";
    private ImageRepo imageRepo;
    private ImageNameConverter imageNameConverter;
    private ImageResizer imageResizer;

    @Autowired
    public ImageService(ImageRepo imageRepo, ImageNameConverter imageNameConverter, ImageResizer imageResizer) {
        this.imageRepo = imageRepo;
        this.imageNameConverter = imageNameConverter;
        this.imageResizer = imageResizer;
    }

    public ResponseEntity<Object> uploadImage(MultipartFile file, String email, String token) throws IOException {
        Map<String, String> response = new HashMap<>();
        if (!TokenService.SERVICE_TOKEN.equals(token)) {
            response.put("isUploadSuccessful", "false");
            response.put("reason", "invalid service token");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        BufferedImage image = ImageIO.read(file.getInputStream());
        BufferedImage image1 = imageResizer.resize(image, 32);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image1, "jpg", baos);

        String smallerImage = Base64.getEncoder().encodeToString(baos.toByteArray());

        String imageName = file.getOriginalFilename();
        Map<String, String> imageMetadata = imageNameConverter.convertImageNameToNameAndFormat(imageName);

        Image imageEntity = Image
                .builder()
                .name(imageMetadata.get("name"))
                .format(imageMetadata.get("format"))
                .email(email)
                .image48Px(smallerImage)
                .upload_time(LocalDateTime.now())
                .build();

        imageRepo.save(imageEntity);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
//        Files.write(fileNameAndPath, baos.toByteArray());
        response.put("isSuccessful", "true");
        response.put("reason", "valid credentials");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Object> getImagesByEmail(String email, String token) {
        Map<String, String> response = new HashMap<>();
        if (!TokenService.SERVICE_TOKEN.equals(token)) {
            response.put("isUploadSuccessful", "false");
            response.put("reason", "invalid service token");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        System.out.println(email);
        Set<Image> images = imageRepo.getImagesByEmail(email);
        System.out.println(images.toString());
        return new ResponseEntity<>(images, HttpStatus.OK);
    }
}
