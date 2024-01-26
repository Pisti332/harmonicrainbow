package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.model.Image;
import com.pisti.harmonicrainbow.model.User;
import com.pisti.harmonicrainbow.repository.ImageRepo;
import com.pisti.harmonicrainbow.repository.UsersRepo;
import com.pisti.harmonicrainbow.service.user.SigninService;
import com.pisti.harmonicrainbow.service.utility.ImageNameConverter;
import com.pisti.harmonicrainbow.service.utility.ImageReader;
import com.pisti.harmonicrainbow.service.utility.ImageResizer;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepo imageRepo;
    private final ImageNameConverter imageNameConverter;
    private final ImageResizer imageResizer;
    private final ImageReader imageReader;
    private final UsersRepo usersRepo;
    private final String UPLOAD_DIRECTORY = System.getenv("UPLOAD_DIRECTORY");

    public ResponseEntity<Object> uploadImage(MultipartFile file, String email) throws IOException {
        Map<String, String> response = new HashMap<>();
        BufferedImage image = ImageIO.read(file.getInputStream());
        BufferedImage image1 = imageResizer.resize(image, 32);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image1, "jpg", baos);

        String smallerImage = Base64.getEncoder().encodeToString(baos.toByteArray());

        String imageName = file.getOriginalFilename();
        Map<String, String> imageMetadata = imageNameConverter.convertImageNameToNameAndFormat(imageName);

        User user = usersRepo.findByEmail(email);

        Image imageEntity = Image
                .builder()
                .name(imageMetadata.get("name"))
                .format(imageMetadata.get("format"))
                .email(email)
                .image48Px(smallerImage)
                .upload_time(LocalDateTime.now())
                .user(user)
                .build();

        imageRepo.save(imageEntity);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        System.out.println(fileNameAndPath);
        Files.write(fileNameAndPath, file.getBytes());
        response.put("isSuccessful", "true");
        response.put("reason", "valid credentials");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Object> getImagesByEmail(String email) {
        Set<Image> images = imageRepo.getImagesByEmail(email);
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    public ResponseEntity<Object> getImageByEmailAndName(String email, String name) {
        try {
            Image image = imageRepo.getImageByEmailAndName(email, name);
            if (image == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("");
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
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("");
        }
    }
}
