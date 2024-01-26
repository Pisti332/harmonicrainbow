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

    public ResponseEntity<Object> changeSaturation(String email, String name, int saturation) {
        if (saturation > 100 || saturation < -100) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("");
        }
        ResponseEntity<Object> imageResponse = imageService.getImageByEmailAndName(email, name);
        if (imageResponse.getStatusCode() == HttpStatus.OK) {
            try {
                ByteArrayResource image = (ByteArrayResource) imageResponse.getBody();
                assert image != null;
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

                ImageAnalyzer imageAnalyzer = new ImageAnalyzer(bufferedImage);
                byte[] colorValues = imageAnalyzer.getColorValues(bufferedImage);
                int width = imageAnalyzer.getWidth();
                int height = imageAnalyzer.getHeight();
                int[] bandOffsets = imageAnalyzer.getBandOffsets();
                String formatName = imageAnalyzer.getFormatName();
                int type = imageAnalyzer.getImageType();

                mutateSaturation(colorValues, type, saturation);

                BufferedImage newImage =
                        imageInitializer.initializeImage(colorValues, width, height, bandOffsets, type);

                ByteArrayResource inputStream = imageConverter.convertImage(newImage, formatName);
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

    private void mutateSaturation(byte[] colorValues, int imageType, int saturation) {
        if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
            if (saturation < 0 && saturation >= -100) {
                for (int i = 0; i < colorValues.length; i += 3) {
                    int red = Byte.toUnsignedInt(colorValues[i + 2]);
                    int green = Byte.toUnsignedInt(colorValues[i + 1]);
                    int blue = Byte.toUnsignedInt(colorValues[i]);

                    Map<String, Float> hsl = hslAndRgbConverter.convertRGBtoHSL(red, green, blue);
                    float newSaturation = hsl.get("s") - hsl.get("s") / 100 * saturation * -1;
                    Map<String, Integer> rgb = hslAndRgbConverter.convertHSLtoRGB(hsl.get("h"),
                            newSaturation,
                            hsl.get("l"));

                    colorValues[i + 2] = rgb.get("r").byteValue();
                    colorValues[i + 1] = rgb.get("g").byteValue();
                    colorValues[i] = rgb.get("b").byteValue();
                }
            } else if (saturation > 0 && saturation <= 100) {
                for (int i = 0; i < colorValues.length; i += 3) {
                    int red = Byte.toUnsignedInt(colorValues[i + 2]);
                    int green = Byte.toUnsignedInt(colorValues[i + 1]);
                    int blue = Byte.toUnsignedInt(colorValues[i]);

                    Map<String, Float> hsl = hslAndRgbConverter.convertRGBtoHSL(red, green, blue);
                    float newSaturation = hsl.get("s") + (100 - hsl.get("s")) / 100 * saturation;
                    Map<String, Integer> rgb = hslAndRgbConverter.convertHSLtoRGB(hsl.get("h"),
                            newSaturation,
                            hsl.get("l"));


                    colorValues[i + 2] = rgb.get("r").byteValue();
                    colorValues[i + 1] = rgb.get("g").byteValue();
                    colorValues[i] = rgb.get("b").byteValue();
                }
            }
        } else if (imageType == BufferedImage.TYPE_4BYTE_ABGR) {
            if (saturation < 0 && saturation >= -100) {
                for (int i = 0; i < colorValues.length; i += 4) {
                    int red = Byte.toUnsignedInt(colorValues[i + 3]);
                    int green = Byte.toUnsignedInt(colorValues[i + 2]);
                    int blue = Byte.toUnsignedInt(colorValues[i + 1]);

                    Map<String, Float> hsl = hslAndRgbConverter.convertRGBtoHSL(red, green, blue);
                    float newSaturation = hsl.get("s") - hsl.get("s") / 100 * saturation * -1;
                    Map<String, Integer> rgb = hslAndRgbConverter.convertHSLtoRGB(hsl.get("h"),
                            newSaturation,
                            hsl.get("l"));

                    colorValues[i + 3] = rgb.get("r").byteValue();
                    colorValues[i + 2] = rgb.get("g").byteValue();
                    colorValues[i + 1] = rgb.get("b").byteValue();
                }

            } else if (saturation > 0 && saturation <= 100) {
                for (int i = 0; i < colorValues.length; i += 4) {
                    int red = Byte.toUnsignedInt(colorValues[i + 3]);
                    int green = Byte.toUnsignedInt(colorValues[i + 2]);
                    int blue = Byte.toUnsignedInt(colorValues[i + 1]);

                    Map<String, Float> hsl = hslAndRgbConverter.convertRGBtoHSL(red, green, blue);
                    float newSaturation = hsl.get("s") + (100 - hsl.get("s")) / 100 * saturation * -1;
                    Map<String, Integer> rgb = hslAndRgbConverter.convertHSLtoRGB(hsl.get("h"),
                            newSaturation,
                            hsl.get("l"));

                    colorValues[i + 3] = rgb.get("r").byteValue();
                    colorValues[i + 2] = rgb.get("g").byteValue();
                    colorValues[i + 1] = rgb.get("b").byteValue();
                }
            }
        } else {
            System.out.println("Unsupported bufferedimage type!");
        }
    }
}
