package com.pisti.harmonicrainbow.service.utility;

import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

@Component
public class ImageInitializer {
    public BufferedImage initializeImage(byte[] colorValues, int width, int height, int[] bandOffsets, int type) {

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
                type);

        newImage.setData(raster);
        return newImage;
    }
}
