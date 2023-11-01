package com.pisti.harmonicrainbow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlackAndWhiteService {
    private ImageService imageService;

    @Autowired
    public BlackAndWhiteService(ImageService imageService) {
        this.imageService = imageService;
    }

    public ResponseEntity<Object> getBlackAndWhite(String email, String name) {
        ResponseEntity<Object> imageResponse = imageService.getImageByEmailAndName(email, name);
        if (imageResponse.getStatusCode() == HttpStatus.OK) {
            try {
                ByteArrayResource image = (ByteArrayResource) imageResponse.getBody();
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
                // TODO
                ByteArrayResource inputStream = new ByteArrayResource(pixels);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .contentLength(inputStream.contentLength())
                        .body(inputStream);
            } catch (IOException e) {
                return new ResponseEntity<>(new HashMap<>(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return imageResponse;
        }
    }
}
