package com.pisti.harmonicrainbow.service;

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

@Service
public class BlackAndWhiteService {
    private ImageService imageService;

    @Autowired
    public BlackAndWhiteService(ImageService imageService) {
        this.imageService = imageService;
    }

    public ResponseEntity<Object> getBlackAndWhite(String email, String name) {
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

                mutateToBlackAndWhite(colorValues, bufferedImage.getType());

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

    private void mutateToBlackAndWhite(byte[] imageData, int imageType) {
        if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < imageData.length; i += 3) {
                int blue = Byte.toUnsignedInt(imageData[i]);
                int green = Byte.toUnsignedInt(imageData[i + 1]);
                int red = Byte.toUnsignedInt(imageData[i + 2]);
                int avg = (red + green + blue) / 3;
                imageData[i] = (byte) avg;
                imageData[i + 1] = (byte) avg;
                imageData[i + 2] = (byte) avg;
            }
        }
        else if (imageType == BufferedImage.TYPE_4BYTE_ABGR) {
            for (int i = 0; i < imageData.length; i += 4) {
                int blue = Byte.toUnsignedInt(imageData[i + 1]);
                int green = Byte.toUnsignedInt(imageData[i + 2]);
                int red = Byte.toUnsignedInt(imageData[i + 3]);
                int avg = (red + green + blue) / 3;
                imageData[i + 1] = (byte) avg;
                imageData[i + 2] = (byte) avg;
                imageData[i + 3] = (byte) avg;
            }
        } else {
            System.out.println("Unsupported bufferedimage type!");
        }
    }
}
