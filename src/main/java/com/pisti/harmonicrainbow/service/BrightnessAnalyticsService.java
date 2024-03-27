package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.exceptions.CorruptImageException;
import com.pisti.harmonicrainbow.exceptions.NoSuchImageException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BrightnessAnalyticsService {
    private final ImageService imageService;

    public Map<String, Integer> getBrightness(String email, String name) {
        ByteArrayResource imageResponse = imageService.getImageByEmailAndName(email, name);
        if (imageResponse != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(imageResponse.getInputStream());
                byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
                int brightness = getBrightness(pixels);
                Map<String, Integer> response = new HashMap<>();
                response.put("brightness", brightness);
                return response;
            }
            catch (IOException e) {
                throw new CorruptImageException("There was a problem while reading the image!");
            }
        }
        else {
            throw new NoSuchImageException("This image doesn't exist!");
        }
    }
    private int getBrightness(byte[] pixelArr) {
        double brightnessSum = 0;
        final int pixelValuesNum = 3;
        final int percentageMultiplier = 100;
        final int maxPixelBrightness = 765;
        final int iterationCycle = 3;

        for(int i = 0; i < pixelArr.length; i += iterationCycle) {
            brightnessSum += (double) (Byte.toUnsignedInt(pixelArr[i]) +
                    Byte.toUnsignedInt(pixelArr[i + 1]) +
                    Byte.toUnsignedInt(pixelArr[i + 2])) /
                    maxPixelBrightness;
        }
        return (int) (brightnessSum / (pixelArr.length / pixelValuesNum) * percentageMultiplier);
    }
}
