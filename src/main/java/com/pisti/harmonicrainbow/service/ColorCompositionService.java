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
public class ColorCompositionService {
    private final ImageService imageService;

    public Map<String, Integer> getColorComposition(String userId, String name) {
        ByteArrayResource imageResponse = imageService.getImageByUserIdAndName(userId, name);
        if (imageResponse != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(imageResponse.getInputStream());
                byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
                return getColorComposition(pixels);
            }
            catch (IOException e) {
                throw new CorruptImageException("There was a problem while reading the image!");
            }
        }
        else {
            throw new NoSuchImageException("No such image!");
        }
    }
    private Map<String, Integer> getColorComposition(byte[] pixels) {
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
        Map<String, Integer> compositions = new HashMap<>();
        compositions.put("red", (int) redPercentage);
        compositions.put("green", (int) greenPercentage);
        compositions.put("blue", (int) bluePercentage);
        return compositions;
    }
}
