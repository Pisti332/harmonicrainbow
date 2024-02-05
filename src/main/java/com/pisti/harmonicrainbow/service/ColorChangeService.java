package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.service.utility.ImageAnalyzer;
import com.pisti.harmonicrainbow.service.utility.ImageConverter;
import com.pisti.harmonicrainbow.service.utility.ImageInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ColorChangeService {
    private final ImageService imageService;
    private final ImageConverter imageConverter;
    private final ImageInitializer imageInitializer;

    public ByteArrayResource changeColors(Map<String, Map<String, Integer>> body, String email, String name) {
        if (body.get("from") == null || body.get("to") == null || body.get("newColor") == null) {
            return null;
        }
        ByteArrayResource imageResponse = imageService.getImageByEmailAndName(email, name);
        if (imageResponse != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(imageResponse.getInputStream());

                ImageAnalyzer imageAnalyzer = new ImageAnalyzer(bufferedImage);
                byte[] colorValues = imageAnalyzer.getColorValues();
                int width = imageAnalyzer.getWidth();
                int height = imageAnalyzer.getHeight();
                int[] bandOffsets = imageAnalyzer.getBandOffsets();
                String formatName = imageAnalyzer.getFormatName();
                int type = imageAnalyzer.getImageType();

                mutateRangeOfPixels(colorValues,
                        body.get("from"),
                        body.get("to"),
                        body.get("newColor"),
                        type);

                BufferedImage newImage =
                        imageInitializer.initializeImage(colorValues, width, height, bandOffsets, type);

                ByteArrayResource inputStream = imageConverter.convertImage(newImage, formatName);

                return inputStream;
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void mutateRangeOfPixels(byte[] pixels, Map<String, Integer> from,
                                     Map<String, Integer> to,
                                     Map<String, Integer> newColor,
                                     int imageType) {
        int iterationCycle = 3;
        float redAvg = (float) (from.get("red") + to.get("red")) / 2;
        float greenAvg = (float) (from.get("green") + to.get("green")) / 2;
        float blueAvg = (float) (from.get("blue") + to.get("blue")) / 2;
        if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < pixels.length; i += iterationCycle) {
                int blue = Byte.toUnsignedInt(pixels[i]);
                int green = Byte.toUnsignedInt(pixels[i + 1]);
                int red = Byte.toUnsignedInt(pixels[i + 2]);

                if (blue >= from.get("blue") && blue <= to.get("blue") &&
                        green >= from.get("green") && green <= to.get("green") &&
                        red >= from.get("red") && red <= to.get("red")) {
                    int newRed = scaleColor(newColor.get("red"), red, redAvg);
                    int newGreen = scaleColor(newColor.get("green"), green, greenAvg);
                    int newBlue = scaleColor(newColor.get("blue"), blue, blueAvg);
                    pixels[i] = (byte) newBlue;
                    pixels[i + 1] = (byte) newGreen;
                    pixels[i + 2] = (byte) newRed;
                }
            }
        }
        if (imageType == BufferedImage.TYPE_4BYTE_ABGR) {
            iterationCycle = 4;
            for (int i = 0; i < pixels.length; i += iterationCycle) {
                int blue = Byte.toUnsignedInt(pixels[i + 1]);
                int green = Byte.toUnsignedInt(pixels[i + 2]);
                int red = Byte.toUnsignedInt(pixels[i + 3]);

                if (blue >= from.get("blue") && blue <= to.get("blue") &&
                        green >= from.get("green") && green <= to.get("green") &&
                        red >= from.get("red") && red <= to.get("red")) {
                    int newRed = scaleColor(newColor.get("red"), red, redAvg);
                    int newGreen = scaleColor(newColor.get("green"), green, greenAvg);
                    int newBlue = scaleColor(newColor.get("blue"), blue, blueAvg);
                    pixels[i + 1] = (byte) newBlue;
                    pixels[i + 2] = (byte) newGreen;
                    pixels[i + 3] = (byte) newRed;
                }
            }
        }
    }

    private int scaleColor(int newColor, int currentColor, float avgColor) {
        int maxChannelValue = 255;
        int calculatedColor = (int) (newColor * currentColor / avgColor);
        if (calculatedColor > maxChannelValue) {
            calculatedColor = maxChannelValue;
        } else if (calculatedColor < 0) {
            calculatedColor = 0;
        }
        return calculatedColor;
    }
}
