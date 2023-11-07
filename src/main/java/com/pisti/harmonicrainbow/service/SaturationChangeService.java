package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.service.utility.HslAndRgbConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaturationChangeService {
    private ImageService imageService;
    private HslAndRgbConverter hslAndRgbConverter;

    @Autowired
    public SaturationChangeService(ImageService imageService, HslAndRgbConverter hslAndRgbConverter) {
        this.imageService = imageService;
        this.hslAndRgbConverter = hslAndRgbConverter;
    }

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
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                byte[] colorValues = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();

                int imageType = bufferedImage.getType();
                String formatName;
                int[] bandOffsets;
                if (imageType == BufferedImage.TYPE_INT_RGB) {
                    bandOffsets = new int[]{0, 1, 2};
                    formatName = "JPEG";
                } else if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
                    bandOffsets = new int[]{2, 1, 0};
                    formatName = "JPEG";
                } else if (imageType == BufferedImage.TYPE_INT_ARGB) {
                    bandOffsets = new int[]{0, 1, 2, 3};
                    formatName = "PNG";
                } else if (imageType == BufferedImage.TYPE_4BYTE_ABGR) {
                    bandOffsets = new int[]{3, 2, 1, 0};
                    formatName = "PNG";
                } else {
                    bandOffsets = new int[]{0, 1, 2};
                    formatName = "JPEG";
                }
                mutateSaturation(colorValues, imageType, saturation);

                DataBuffer dataBuffer = new DataBufferByte(colorValues, colorValues.length);

                WritableRaster raster = WritableRaster.createInterleavedRaster(dataBuffer,
                        width,
                        height,
                        width * bandOffsets.length,
                        bandOffsets.length,
                        bandOffsets,
                        null);

                BufferedImage newImage = new BufferedImage(width,
                        height,
                        bufferedImage.getType());

                newImage.setData(raster);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(newImage, formatName, baos);
                byte[] bytes = baos.toByteArray();

                ByteArrayResource inputStream = new ByteArrayResource(bytes);
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
