package com.pisti.harmonicrainbow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ColorChangeService {
    private ImageService imageService;

    @Autowired
    public ColorChangeService(ImageService imageService) {
        this.imageService = imageService;
    }

    public ResponseEntity<Object> changeColors(Map<String, Map<String, Integer>> body, String email, String name) {
        Map<String, String> response = new HashMap<>();
        if (body.get("from") == null || body.get("to") == null || body.get("newColor") == null) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<Object> imageResponse = imageService.getImageByEmailAndName(email, name);
        if (imageResponse.getStatusCode() == HttpStatus.OK) {
            try {
                ByteArrayResource image = (ByteArrayResource) imageResponse.getBody();
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
                //TODO create service
                //TODO convert to image
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>(new HashMap<>(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return imageResponse;
        }
    }
}
