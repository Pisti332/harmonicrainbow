package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.exceptions.NoSuchImageException;
import com.pisti.harmonicrainbow.model.Image;
import com.pisti.harmonicrainbow.model.User;
import com.pisti.harmonicrainbow.repository.ImageRepo;
import com.pisti.harmonicrainbow.repository.UsersRepo;
import com.pisti.harmonicrainbow.service.utility.ImageNameConverter;
import com.pisti.harmonicrainbow.service.utility.ImageReader;
import com.pisti.harmonicrainbow.service.utility.ImageResizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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

    public Map<String, String> uploadImage(MultipartFile file, String email) throws IOException {
        int smallImagePixelSize = 32;
        Map<String, String> response = new HashMap<>();
        BufferedImage image = ImageIO.read(file.getInputStream());
        BufferedImage image1 = imageResizer.resize(image, smallImagePixelSize);

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
                .image48Px(smallerImage)
                .upload_time(LocalDateTime.now())
                .user(user)
                .build();

        imageRepo.save(imageEntity);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
        response.put("isSuccessful", "true");
        response.put("reason", "valid credentials");
        return response;
    }

    public Set<Image> getImagesByEmail(String email) {
        User user = usersRepo.findByEmail(email);
        Set<Image> images = imageRepo.getImageByUser(user);
        return images;
    }

    public ByteArrayResource getImageByUserIdAndName(String userId, String name) {
        try {
            User user = usersRepo.findByUserId(UUID.fromString(userId));
            Image image = imageRepo.getImageByUserAndName(user, name);
            if (image == null) {
                return null;
            }
            BufferedImage bufferedImage = imageReader.readImage(name, image.getFormat());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, image.getFormat(), baos);
            byte[] bytes = baos.toByteArray();
            return new ByteArrayResource(bytes);

        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public ResponseEntity<Object> deleteImageByNameAndUserId(String userId, String name) {
        User user;
        Integer isDelSuccessful;
        try {
            user = usersRepo.findByUserId(UUID.fromString(userId));
            var imageFormat = imageRepo.getImageByUserAndName(user, name).getFormat();
            isDelSuccessful = imageRepo.deleteByUserAndName(user, name);
            var fileToDelete = UPLOAD_DIRECTORY + "/" + name + "." + imageFormat;
            File file = new File(fileToDelete);
            if (!file.delete()) {
                throw new FileNotFoundException("The image is not present in the database!");
            }
        }
        catch (Exception e) {
            throw new NoSuchImageException("Something went wrong!");
        }
        if (user == null || isDelSuccessful == 0) {
            throw new NoSuchImageException("Image doesn't exist with this name and email combination!");
        }
        else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    public List<Image> getAllImages() {
        return imageRepo.findAll();
    }
}
