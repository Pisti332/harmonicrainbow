package com.pisti.harmonicrainbow.service;

import com.pisti.harmonicrainbow.exceptions.CorruptImageException;
import com.pisti.harmonicrainbow.exceptions.NoSuchImageException;
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

                mutateToBlackAndWhite(colorValues, bufferedImage.getType());

                BufferedImage newImage =
                        imageInitializer.initializeImage(colorValues, width, height, bandOffsets, type);

                return imageConverter.convertImage(newImage, formatName);

            } catch (IOException e) {
                throw new CorruptImageException("There was a problem while reading the image!");
            }
        } else {
            throw new NoSuchImageException("No such image!");
        }
    }

    private void mutateToBlackAndWhite(byte[] imageData, int imageType) throws IOException {
        int iterationCycle = 3;
        if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < imageData.length; i += iterationCycle) {
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
            iterationCycle = 4;
            for (int i = 0; i < imageData.length; i += iterationCycle) {
                int blue = Byte.toUnsignedInt(imageData[i + 1]);
                int green = Byte.toUnsignedInt(imageData[i + 2]);
                int red = Byte.toUnsignedInt(imageData[i + 3]);
                int avg = (red + green + blue) / 3;
                imageData[i + 1] = (byte) avg;
                imageData[i + 2] = (byte) avg;
                imageData[i + 3] = (byte) avg;
            }
        }
        else throw new IOException();
    }
}
