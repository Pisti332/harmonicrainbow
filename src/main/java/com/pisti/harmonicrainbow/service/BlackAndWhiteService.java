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
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BlackAndWhiteService {
    private final ImageService imageService;
    private final ImageInitializer imageInitializer;
    private final ImageConverter imageConverter;

    public ByteArrayResource getBlackAndWhite(String email, String name) {
        ResponseEntity<Object> imageResponse = imageService.getImageByEmailAndName(email, name);
        if (imageResponse.getStatusCode() == HttpStatus.OK) {
            try {
                ByteArrayResource image = (ByteArrayResource) imageResponse.getBody();
                assert image != null;
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

                ImageAnalyzer imageAnalyzer = new ImageAnalyzer(bufferedImage);
                byte[] colorValues = imageAnalyzer.getColorValues();
                int width = imageAnalyzer.getWidth();
                int height = imageAnalyzer.getHeight();
                int[] bandOffsets = imageAnalyzer.getBandOffsets();
                String formatName = imageAnalyzer.getFormatName();
                int type = imageAnalyzer.getImageType();

                mutateToBlackAndWhite(colorValues, bufferedImage.getType());

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
