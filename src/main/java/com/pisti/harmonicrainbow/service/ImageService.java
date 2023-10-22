package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.model.Image;
import com.pisti.harmonicrainbow.repository.ImageRepo;
import com.pisti.harmonicrainbow.service.user.TokenService;
import com.pisti.harmonicrainbow.service.utility.ImageNameConverter;
import com.pisti.harmonicrainbow.service.utility.ImageReader;
import com.pisti.harmonicrainbow.service.utility.ImageResizer;
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
    private ImageRepo imageRepo;
    private ImageNameConverter imageNameConverter;
    private ImageResizer imageResizer;
    private ImageReader imageReader;
    private TokenService tokenService;
    private final String UPLOAD_DIRECTORY = System.getenv("UPLOAD_DIRECTORY");

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
        System.out.println(fileNameAndPath);
        Files.write(fileNameAndPath, file.getBytes());
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
        catch (Exception e) {
            response.put("isDownloadSuccessful", "false");
            response.put("reason", "invalid token format");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentLength(response.size())
                    .body(response);
        }
        if (!isTokenValid) {
            response.put("isDownloadSuccessful", "false");
            response.put("reason", "invalid token");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentLength(response.size())
                    .body(response);

        }
        try {
            Image image = imageRepo.getImageByEmailAndName(email, name);
            if (image == null) {
                response.put("isDownloadSuccessful", "false");
                response.put("reason", "no such image");
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentLength(response.size())
                        .body(response);
            }
            BufferedImage bufferedImage = imageReader.readImage(name, image.getFormat());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, image.getFormat(), baos);
            byte[] bytes = baos.toByteArray();
            ByteArrayResource inputStream = new ByteArrayResource(bytes);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentLength(inputStream.contentLength())
                    .body(inputStream);

        } catch (Exception e) {
            response.put("isDownloadSuccessful", "false");
            response.put("reason", "there was an error downloading the image");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentLength(response.size())
                    .body(response);
        }
    }
}
