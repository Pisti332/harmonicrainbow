package com.pisti.harmonicrainbow.service.utility;

import lombok.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

@Getter
public class ImageAnalyzer {
    private final String formatName;
    private final int imageType;
    private final int[] bandOffsets;
    private final int width;
    private final int height;
    private final BufferedImage bufferedImage;

    public ImageAnalyzer(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.imageType = bufferedImage.getType();
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
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
    }

    public byte[] getColorValues() throws IOException {
        return ((DataBufferByte) this.bufferedImage.getRaster().getDataBuffer()).getData();
    }
}
