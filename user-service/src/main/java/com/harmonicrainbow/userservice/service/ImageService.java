package com.harmonicrainbow.userservice.service;

import com.harmonicrainbow.userservice.model.Image;
import com.harmonicrainbow.userservice.repository.ImageRepo;
import com.harmonicrainbow.userservice.service.utility.ImageNameConverter;
import com.harmonicrainbow.userservice.service.utility.ImageReader;
import com.harmonicrainbow.userservice.service.utility.ImageResizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ImageService {
    public static final String UPLOAD_DIRECTORY = "user-service\\src\\main\\resources\\images\\";
    private ImageRepo imageRepo;
    private ImageNameConverter imageNameConverter;
    private ImageResizer imageResizer;
    private ImageReader imageReader;
    private TokenService tokenService;

    @Autowired
    public ImageService(ImageRepo imageRepo, ImageNameConverter imageNameConverter, ImageResizer imageResizer, ImageReader imageReader, TokenService tokenService) {
        this.imageRepo = imageRepo;
        this.imageNameConverter = imageNameConverter;
        this.imageResizer = imageResizer;
        this.imageReader = imageReader;
        this.tokenService = tokenService;
    }

    public ResponseEntity<Object> uploadImage(MultipartFile file, String email, String token) throws IOException {
        Map<String, String> response = new HashMap<>();
        boolean isTokenValid;
        try {
            isTokenValid = tokenService.checkIfTokenExists(UUID.fromString(token));
        }
        catch (Exception e) {
            response.put("isUploadSuccessful", "false");
            response.put("reason", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!isTokenValid) {
            response.put("isUploadSuccessful", "false");
            response.put("reason", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
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
        try {
            if (!tokenService.checkIfTokenExists(UUID.fromString(token))) {
                response.put("isDownloadSuccessful", "false");
                response.put("reason", "invalid token");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception e) {
            response.put("isDownloadSuccessful", "false");
            response.put("reason", "invalid token format");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Set<Image> images = imageRepo.getImagesByEmail(email);
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    public ResponseEntity<Object> getImageByEmailAndName(String email, String name, String token) {
        Map<String, String> response = new HashMap<>();
        boolean isTokenValid;
        try {
            isTokenValid = tokenService.checkIfTokenExists(UUID.fromString(token));
        }
        catch (NumberFormatException e) {
            response.put("isDownloadSuccessful", "false");
            response.put("reason", "invalid token format");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!isTokenValid) {
            response.put("isDownloadSuccessful", "false");
            response.put("reason", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        try {
            Image image = imageRepo.getImageByEmailAndName(email, name);
            System.out.println(image);
            if (image == null) {
                response.put("isSuccessful", "false");
                response.put("reason", "no such image");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            BufferedImage bufferedImage = imageReader.readImage(name, image.getFormat());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, image.getFormat(), baos);
            byte[] bytes = baos.toByteArray();
            ByteArrayResource inputStream = new ByteArrayResource(bytes);
//            response.put("isUploadSuccessful", "false");
//            response.put("reason", "no such image");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentLength(inputStream.contentLength())
                    .body(inputStream);

        } catch (Exception e) {
            response.put("isUploadSuccessful", "false");
            response.put("reason", "there was an error");
            return new ResponseEntity<>("response", HttpStatus.BAD_REQUEST);
        }
    }
}
