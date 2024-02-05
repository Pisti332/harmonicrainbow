package com.pisti.harmonicrainbow.service;

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
public class ColorCompositionService {
    private final ImageService imageService;

    public Map<String, Float> getColorComposition(String email, String name) {
        ByteArrayResource imageResponse = imageService.getImageByEmailAndName(email, name);
        if (imageResponse != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(imageResponse.getInputStream());
                byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
                Map<String, Float> response = getColorComposition(pixels);
                return response;
            }
            catch (IOException e) {
                return null;
            }
        }
        else {
            return null;
        }
    }
    private Map<String, Float> getColorComposition(byte[] pixels) {
        long brightnessSum = 0;
        long redSum = 0;
        long greenSum = 0;
        long blueSum = 0;
        for (int i = 0; i < pixels.length; i++) {
            brightnessSum += Byte.toUnsignedInt(pixels[i]);
            if ((i + 1) % 2 == 0) {
                redSum += Byte.toUnsignedInt(pixels[i]);
            }
            else if ((i + 1) % 3 == 0) {
                blueSum += Byte.toUnsignedInt(pixels[i]);
            }
            else {
                greenSum += Byte.toUnsignedInt(pixels[i]);
            }
        }

        float redPercentage = (float) redSum / brightnessSum * 100;
        float greenPercentage = (float) greenSum / brightnessSum * 100;
        float bluePercentage = (float) blueSum / brightnessSum * 100;
        Map<String, Float> compositions = new HashMap<>();
        compositions.put("red", redPercentage);
        compositions.put("green", greenPercentage);
        compositions.put("blue", bluePercentage);
        return compositions;
    }
}
