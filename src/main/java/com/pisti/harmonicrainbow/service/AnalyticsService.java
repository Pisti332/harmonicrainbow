package com.pisti.harmonicrainbow.service;

import org.springframework.core.io.ByteArrayResource;
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
public class AnalyticsService {
    private final ImageService imageService;

    public AnalyticsService(ImageService imageService) {
        this.imageService = imageService;
    }

    public ResponseEntity<Object> getBrightness(String email, String name, String token) {
        ResponseEntity<Object> imageResponse = imageService.getImageByEmailAndName(email, name, token);
        if (imageResponse.getStatusCode() == HttpStatus.OK) {
            try {
                ByteArrayResource image = (ByteArrayResource) imageResponse.getBody();
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
                int brightness = getBrightness(pixels);
                Map<String, Integer> response = new HashMap<>();
                response.put("brightness", brightness);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            catch (IOException e) {
                return new ResponseEntity<>(new HashMap<>(), HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return imageResponse;
        }
    }
    private int getBrightness(byte[] pixelArr) {
        double brightnessSum = 0;
        final int pixelValuesNum = 3;
        final int percentageMultiplier = 100;
        final int maxPixelBrightness = 765;
        for(int i = 0; i < pixelArr.length; i += 3) {
            brightnessSum += (double) (Byte.toUnsignedInt(pixelArr[i]) +
                    Byte.toUnsignedInt(pixelArr[i + 1]) +
                    Byte.toUnsignedInt(pixelArr[i + 2])) /
                    maxPixelBrightness;
        }
        return (int) (brightnessSum / (pixelArr.length / pixelValuesNum) * percentageMultiplier);
    }
}
