package com.pisti.harmonicrainbow.service;

import org.apache.coyote.Response;
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
public class SaturationChangeService {
    private ImageService imageService;

    public SaturationChangeService(ImageService imageService) {
        this.imageService = imageService;
    }

    public ResponseEntity<Object> changeSaturation(String email, String name, int saturation) {
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

                mutateSaturation(colorValues, bufferedImage.getType(), saturation);

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

    private void mutateSaturation(byte[] colorValues, int type, int saturation) {
    }
}
