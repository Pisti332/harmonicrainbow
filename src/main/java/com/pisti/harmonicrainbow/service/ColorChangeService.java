package com.pisti.harmonicrainbow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ColorChangeService {
    private final ImageService imageService;

    public ResponseEntity<Object> changeColors(Map<String, Map<String, Integer>> body, String email, String name) {
        System.out.println(body.toString());
        System.out.println(email);
        System.out.println(name);
        if (body.get("from") == null || body.get("to") == null || body.get("newColor") == null) {
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

                mutateRangeOfPixels(colorValues,
                        body.get("from"),
                        body.get("to"),
                        body.get("newColor"),
                        imageType);

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

    private void mutateRangeOfPixels(byte[] pixels, Map<String, Integer> from,
                                     Map<String, Integer> to,
                                     Map<String, Integer> newColor,
                                     int imageType) {
        float redAvg = (float) (from.get("red") + to.get("red")) / 2;
        float greenAvg = (float) (from.get("green") + to.get("green")) / 2;
        float blueAvg = (float) (from.get("blue") + to.get("blue")) / 2;
        if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < pixels.length; i += 3) {
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
            for (int i = 0; i < pixels.length; i += 4) {
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
        } else {
            System.out.println("Unsupported bufferedimage type!");
        }
    }

    private int scaleColor(int newColor, int currentColor, float avgColor) {
        int calculatedColor = (int) (newColor * currentColor / avgColor);
        if (calculatedColor > 255) {
            calculatedColor = 255;
        } else if (calculatedColor < 0) {
            calculatedColor = 0;
        }
        return calculatedColor;
    }
}
