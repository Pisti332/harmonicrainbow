package com.harmonicrainbow.userservice.service.utility;

import com.harmonicrainbow.userservice.service.ImageService;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageReader {
    public BufferedImage readImage(String name, String format) {
        String path = ImageService.UPLOAD_DIRECTORY + "/" + name + "." + format;
        try {
            return ImageIO.read(new File(path));
        }
        catch (IOException e) {
            return null;
        }
    }
}
