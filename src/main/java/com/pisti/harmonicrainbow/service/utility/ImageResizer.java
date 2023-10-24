package com.pisti.harmonicrainbow.service.utility;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class ImageResizer {
    public BufferedImage resize(BufferedImage bufferedImage, int newDimension) {
        int originalWidth = bufferedImage.getWidth();
        int originalHeight = bufferedImage.getHeight();
        float scale;

        int newWidth;
        int newHeight;
        if (originalHeight > originalWidth) {
            scale = (float) originalHeight / originalWidth;
            newHeight = Math.round(newDimension * scale);
            newWidth = newDimension;
        }
        else {
            scale = (float) originalWidth / originalHeight;
            newHeight = newDimension;
            newWidth = Math.round(newDimension * scale);
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(bufferedImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return resizedImage;
    }
}
