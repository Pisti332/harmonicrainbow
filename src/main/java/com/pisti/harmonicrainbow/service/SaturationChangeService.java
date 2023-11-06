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
import java.util.Map;

@Service
public class SaturationChangeService {
    private ImageService imageService;

    @Autowired
    public SaturationChangeService(ImageService imageService) {
        this.imageService = imageService;
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
        int absSaturation = Math.abs(saturation);
        if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
            if (saturation < 0 && saturation >= -100) {
                for (int i = 0; i < colorValues.length; i += 3) {
                    int red = Byte.toUnsignedInt(colorValues[i + 2]);
                    int green = Byte.toUnsignedInt(colorValues[i + 1]);
                    int blue = Byte.toUnsignedInt(colorValues[i]);
                    int bw = (red + green + blue) / 3;
                    int redDiff = bw - red;
                    int greenDiff = bw - green;
                    int blueDiff = bw - blue;
                    float redPerPercent = (float) redDiff / 100;
                    float greenPerPercent = (float) greenDiff / 100;
                    float bluePerPercent = (float) blueDiff / 100;
                    colorValues[i + 2] = (byte) (absSaturation * redPerPercent + red);
                    colorValues[i + 1] = (byte) (absSaturation * greenPerPercent + green);
                    colorValues[i] = (byte) (absSaturation * bluePerPercent + blue);
                }
            } else if (saturation > 0 && saturation <= 100) {
                for (int i = 0; i < colorValues.length; i += 3) {
                    int red = Byte.toUnsignedInt(colorValues[i + 2]);
                    int green = Byte.toUnsignedInt(colorValues[i + 1]);
                    int blue = Byte.toUnsignedInt(colorValues[i]);
                    int maxFromRedGreen = Math.max(red, green);
                    int max = Math.max(blue, maxFromRedGreen);
                    float maxDiff = (float) 255 / max - 1;
                    float maxDiffPerPercent = maxDiff / 100;

                    int brightness = red + green + blue;

                    int newRed = (int) ((saturation * maxDiffPerPercent + 1) * red);
                    int newGreen = (int) ((saturation * maxDiffPerPercent + 1) * green);
                    int newBlue = (int) ((saturation * maxDiffPerPercent + 1) * blue);

                    int newBrightness = newRed + newGreen + newBlue;

                    int brightnessDiff = (newBrightness - brightness) / 3;

                    colorValues[i + 2] = (byte) (Math.max(newRed - brightnessDiff, 0));
                    colorValues[i + 1] = (byte) (Math.max(newGreen - brightnessDiff, 0));
                    colorValues[i] = (byte) (Math.max(newBlue - brightnessDiff, 0));
                }
            }
        } else if (imageType == BufferedImage.TYPE_4BYTE_ABGR) {
            if (saturation < 0 && saturation >= -100) {
                for (int i = 0; i < colorValues.length; i += 4) {
                    int red = Byte.toUnsignedInt(colorValues[i + 3]);
                    int green = Byte.toUnsignedInt(colorValues[i + 2]);
                    int blue = Byte.toUnsignedInt(colorValues[i + 1]);
                    int bw = (red + green + blue) / 3;
                    int redDiff = bw - red;
                    int greenDiff = bw - green;
                    int blueDiff = bw - blue;
                    float redPerPercent = (float) redDiff / 100;
                    float greenPerPercent = (float) greenDiff / 100;
                    float bluePerPercent = (float) blueDiff / 100;
                    colorValues[i + 3] = (byte) (absSaturation * redPerPercent + red);
                    colorValues[i + 2] = (byte) (absSaturation * greenPerPercent + green);
                    colorValues[i + 1] = (byte) (absSaturation * bluePerPercent + blue);
                }
            } else if (saturation > 0 && saturation <= 100) {
                for (int i = 0; i < colorValues.length; i += 4) {
                    int red = Byte.toUnsignedInt(colorValues[i + 3]);
                    int green = Byte.toUnsignedInt(colorValues[i + 2]);
                    int blue = Byte.toUnsignedInt(colorValues[i + 1]);
                    int max1 = Math.max(red, green);
                    int max = Math.max(blue, max1);

                    float maxDiff = (float) 255 / max - 1;
                    float maxDiffPerPercent = maxDiff / 100;

                    int brightness = red + green + blue;

                    int newRed = (int) ((saturation * maxDiffPerPercent + 1) * red);
                    int newGreen = (int) ((saturation * maxDiffPerPercent + 1) * green);
                    int newBlue = (int) ((saturation * maxDiffPerPercent + 1) * blue);

                    int newBrightness = newRed + newGreen + newBlue;

                    int brightnessDiff = (newBrightness - brightness) / 3;

                    colorValues[i + 3] = (byte) (Math.max(newRed - brightnessDiff, 0));
                    colorValues[i + 2] = (byte) (Math.max(newGreen - brightnessDiff, 0));
                    colorValues[i + 1] = (byte) (Math.max(newBlue - brightnessDiff, 0));
                }
            }
        } else {
            System.out.println("Unsupported bufferedimage type!");
        }
    }
}
