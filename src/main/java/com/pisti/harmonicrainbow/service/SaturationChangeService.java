package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.service.utility.HslAndRgbConverter;
import com.pisti.harmonicrainbow.service.utility.ImageAnalyzer;
import com.pisti.harmonicrainbow.service.utility.ImageConverter;
import com.pisti.harmonicrainbow.service.utility.ImageInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SaturationChangeService {
    private final ImageService imageService;
    private final HslAndRgbConverter hslAndRgbConverter;
    private final ImageInitializer imageInitializer;
    private final ImageConverter imageConverter;

    public ByteArrayResource changeSaturation(String email, String name, int saturation) {
        if (saturation > 100 || saturation < -100) {
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

                mutateSaturation(colorValues, type, saturation);

                BufferedImage newImage =
                        imageInitializer.initializeImage(colorValues, width, height, bandOffsets, type);

                return imageConverter.convertImage(newImage, formatName);
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void mutateSaturation(byte[] colorValues, int imageType, int saturation) throws IOException {
        if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < colorValues.length; i += 3) {
                int red = Byte.toUnsignedInt(colorValues[i + 2]);
                int green = Byte.toUnsignedInt(colorValues[i + 1]);
                int blue = Byte.toUnsignedInt(colorValues[i]);

                Map<String, Float> hsl = hslAndRgbConverter.convertRGBtoHSL(red, green, blue);
                float newSaturation = hsl.get("s");
                if (saturation < 0 && saturation >= -100) {
                    newSaturation = hsl.get("s") - hsl.get("s") / 100 * saturation * -1;
                } else if (saturation > 0 && saturation <= 100) {
                    newSaturation = hsl.get("s") + (100 - hsl.get("s")) / 100 * saturation;
                }
                Map<String, Integer> rgb = hslAndRgbConverter.convertHSLtoRGB(hsl.get("h"),
                        newSaturation,
                        hsl.get("l"));

                colorValues[i + 2] = rgb.get("r").byteValue();
                colorValues[i + 1] = rgb.get("g").byteValue();
                colorValues[i] = rgb.get("b").byteValue();
            }
        } else if (imageType == BufferedImage.TYPE_4BYTE_ABGR) {
            for (int i = 0; i < colorValues.length; i += 4) {
                int red = Byte.toUnsignedInt(colorValues[i + 3]);
                int green = Byte.toUnsignedInt(colorValues[i + 2]);
                int blue = Byte.toUnsignedInt(colorValues[i + 1]);

                Map<String, Float> hsl = hslAndRgbConverter.convertRGBtoHSL(red, green, blue);
                float newSaturation = hsl.get("s");
                if (saturation < 0 && saturation >= -100) {
                    newSaturation = hsl.get("s") - hsl.get("s") / 100 * saturation * -1;
                } else if (saturation > 0 && saturation <= 100) {
                    newSaturation = hsl.get("s") + (100 - hsl.get("s")) / 100 * saturation * -1;
                }
                Map<String, Integer> rgb = hslAndRgbConverter.convertHSLtoRGB(hsl.get("h"),
                        newSaturation,
                        hsl.get("l"));

                colorValues[i + 3] = rgb.get("r").byteValue();
                colorValues[i + 2] = rgb.get("g").byteValue();
                colorValues[i + 1] = rgb.get("b").byteValue();
            }
        } else {
            throw new IOException();
        }
    }
}
