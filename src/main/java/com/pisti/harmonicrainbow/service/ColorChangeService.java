package com.pisti.harmonicrainbow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ColorChangeService {
    private ImageService imageService;

    @Autowired
    public ColorChangeService(ImageService imageService) {
        this.imageService = imageService;
    }

    public ResponseEntity<Object> changeColors(Map<String, Map<String, Integer>> body, String email, String name) {
        Map<String, String> response = new HashMap<>();
        if (body.get("from") == null || body.get("to") == null || body.get("newColor") == null) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<Object> imageResponse = imageService.getImageByEmailAndName(email, name);
        if (imageResponse.getStatusCode() == HttpStatus.OK) {
            try {
                ByteArrayResource image = (ByteArrayResource) imageResponse.getBody();
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                byte[] colorValues = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

                mutateRangeOfPixels(colorValues, body.get("from"), body.get("to"), body.get("newColor"));

                System.out.println(colorValues.length);

                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();
                // Create a DataBuffer from the pixel byte array
                DataBuffer dataBuffer = new DataBufferByte(colorValues, colorValues.length);

                System.out.println(bufferedImage.getType());
                String formatName;
                int[] bandOffsets;
                if (bufferedImage.getType() == BufferedImage.TYPE_INT_RGB) {
                    bandOffsets = new int[]{0, 1, 2};
                    formatName = "JPEG";
                }
                else if (bufferedImage.getType() == BufferedImage.TYPE_3BYTE_BGR) {
                    bandOffsets = new int[]{2, 1, 0};
                    formatName = "JPEG";
                }
                else if (bufferedImage.getType() == BufferedImage.TYPE_INT_ARGB) {
                    bandOffsets = new int[]{0, 1, 2, 3};
                    formatName = "PNG";
                }
                else if (bufferedImage.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
                    bandOffsets = new int[]{3, 2, 1, 0};
                    formatName = "PNG";
                }
                else if (bufferedImage.getType() == BufferedImage.TYPE_INT_ARGB) {
                    bandOffsets = new int[]{0, 1, 2, 3};
                    formatName = "PNG";
                }
                else {
                    bandOffsets = new int[]{0, 1, 2};
                    formatName = "JPEG";
                }
                // Define the SampleModel for the specified image type
                WritableRaster raster = WritableRaster.createInterleavedRaster(dataBuffer,
                        width,
                        height,
                        width * bandOffsets.length,
                        bandOffsets.length,
                        bandOffsets,
                        null);

                // Create the BufferedImage using the ColorModel and Raster
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
                                        Map<String, Integer> newColor) {
//        float redAvg = (float) (from.get("red") + to.get("red")) / 2;
//        float greenAvg = (float) (from.get("green") + to.get("green")) / 2;
//        float blueAvg = (float) (from.get("blue") + to.get("blue")) / 2;
        for (int i = 0; i < pixels.length; i++) {
            int current = Byte.toUnsignedInt(pixels[i]);
            if ((i + 1) % 2 == 0 && current >= from.get("green") && current <= to.get("green")) {
                pixels[i] = newColor.get("green").byteValue();
            }
            else if ((i + 1) % 3 == 0 && current >= from.get("blue") && current <= to.get("blue")) {
                pixels[i] = newColor.get("blue").byteValue();
            }
            else if (current >= from.get("red") && current <= to.get("red")) {
                pixels[i] = newColor.get("red").byteValue();
            }
        }
    }
}
