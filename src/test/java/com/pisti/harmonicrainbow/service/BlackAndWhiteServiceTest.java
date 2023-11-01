package com.pisti.harmonicrainbow.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BlackAndWhiteServiceTest {
    @Autowired
    private BlackAndWhiteService blackAndWhiteService;
    private BufferedImage bufferedImage;

    @MockBean
    private ImageService imageService;

    @BeforeAll
    void readImage() throws IOException {
        String image = "src/test/java/com/pisti/harmonicrainbow/service/images/test.jpg";
        this.bufferedImage = ImageIO.read(new File(image));
    }

    @BeforeEach
    void setup() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(this.bufferedImage, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        ByteArrayResource inputStream = new ByteArrayResource(bytes);
        ResponseEntity<Object> response = ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);
        when(imageService.getImageByEmailAndName("test@test.com", "test")).thenReturn(response);
    }

    @Test
    @WithMockUser(username = "testuser")
    void isImageBlackAndWhite() throws IOException {
        ResponseEntity<Object> response = blackAndWhiteService.getBlackAndWhite("test@test.com", "test");
        ByteArrayResource image = (ByteArrayResource) response.getBody();
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        byte[] colorValues = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        if (bufferedImage.getType() == BufferedImage.TYPE_INT_ARGB ||
                bufferedImage.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
            for (int i = 0; i < colorValues.length; i += 4) {
                byte f = colorValues[i + 3];
                byte s = colorValues[i + 2];
                byte t = colorValues[i + 1];
                if (f != s || f != t) {
                    fail();
                }
            }
        } else if (bufferedImage.getType() == BufferedImage.TYPE_3BYTE_BGR ||
                bufferedImage.getType() == BufferedImage.TYPE_INT_RGB) {
            for (int i = 0; i < colorValues.length; i += 3) {
                byte f = colorValues[i];
                byte s = colorValues[i + 1];
                byte t = colorValues[i + 2];
                if (f != s || f != t) {
                    fail();
                }
            }
        }

    }
}
