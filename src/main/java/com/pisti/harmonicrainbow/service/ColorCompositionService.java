package com.pisti.harmonicrainbow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseEntity<Object> getColorComposition(String email, String name) {
        ResponseEntity<Object> imageResponse = imageService.getImageByEmailAndName(email, name);
        if (imageResponse.getStatusCode() == HttpStatus.OK) {
            try {
                ByteArrayResource image = (ByteArrayResource) imageResponse.getBody();
                assert image != null;
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
                Map<String, Float> response = getColorComposition(pixels);
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
        System.out.println(redSum);
        System.out.println(greenSum);
        System.out.println(blueSum);
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
