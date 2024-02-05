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
    private BufferedImage bufferedImageJpg;
    private BufferedImage bufferedImagePng;

    @MockBean
    private ImageService imageService;

    @BeforeAll
    void readImage() throws IOException {
        String imageJpg = "src/test/java/com/pisti/harmonicrainbow/service/images/test.jpg";
        this.bufferedImageJpg = ImageIO.read(new File(imageJpg));
        String imagePng = "src/test/java/com/pisti/harmonicrainbow/service/images/test1.png";
        this.bufferedImagePng = ImageIO.read(new File(imagePng));
    }

    @BeforeEach
    void setup() throws IOException {
        ByteArrayOutputStream baosJpg = new ByteArrayOutputStream();
        ImageIO.write(this.bufferedImageJpg, "jpg", baosJpg);
        byte[] bytesJpg = baosJpg.toByteArray();
        ByteArrayResource inputStreamJpg = new ByteArrayResource(bytesJpg);
        ResponseEntity<Object> responseJpg = ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStreamJpg.contentLength())
                .body(inputStreamJpg);

        ByteArrayOutputStream baosPng = new ByteArrayOutputStream();
        ImageIO.write(this.bufferedImagePng, "png", baosPng);
        byte[] bytesPng = baosPng.toByteArray();
        ByteArrayResource inputStreamPng = new ByteArrayResource(bytesPng);
        ResponseEntity<Object> responsePng = ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStreamPng.contentLength())
                .body(inputStreamPng);
        ResponseEntity<Object> badResponse = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("");
        when(imageService.getImageByEmailAndName("test@test.com", "test")).thenReturn(responseJpg);
        when(imageService.getImageByEmailAndName("test@test.com", "test1")).thenReturn(responsePng);
        when(imageService.getImageByEmailAndName("test123@test.com", "asd")).thenReturn(badResponse);
    }

    @Test
    @WithMockUser(username = "testuser")
    void isImageBlackAndWhiteJpg() throws IOException {
        ByteArrayResource response = blackAndWhiteService.getBlackAndWhite("test@test.com", "test");

        BufferedImage bufferedImage = ImageIO.read(response.getInputStream());
        byte[] colorValues = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < colorValues.length; i += 3) {
            byte f = colorValues[i];
            byte s = colorValues[i + 1];
            byte t = colorValues[i + 2];
            if (f != s || f != t) {
                fail();
            }
        }
    }

    @Test
    @WithMockUser(username = "testuser")
    void isImageBlackAndWhitePng() throws IOException {
        ByteArrayResource response = blackAndWhiteService.getBlackAndWhite("test@test.com", "test1");

        BufferedImage bufferedImage = ImageIO.read(response.getInputStream());
        byte[] colorValues = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < colorValues.length; i += 3) {
            byte f = colorValues[i];
            byte s = colorValues[i + 1];
            byte t = colorValues[i + 2];
            if (f != s || f != t) {
                fail();
            }
        }
    }

    @Test
    @WithMockUser(username = "testuser")
    void isJpgDataArraySameLength() throws IOException {
        ByteArrayResource response = blackAndWhiteService.getBlackAndWhite("test@test.com",
                "test");

        BufferedImage bufferedImage = ImageIO.read(response.getInputStream());
        byte[] colorValues = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        byte[] colorValuesBefore = ((DataBufferByte) this.bufferedImageJpg.getRaster().getDataBuffer()).getData();
        if (colorValues.length != colorValuesBefore.length) {
            fail();
        }
    }

    @Test
    @WithMockUser(username = "testuser")
    void areJpgDimensionsTheSame() throws IOException {
        ByteArrayResource response = blackAndWhiteService.getBlackAndWhite("test@test.com",
                "test");

        BufferedImage bufferedImage = ImageIO.read(response.getInputStream());
        if (bufferedImage.getHeight() != this.bufferedImageJpg.getHeight() ||
                bufferedImage.getWidth() != this.bufferedImageJpg.getWidth()) {
            fail();
        }
    }

    @Test
    @WithMockUser(username = "testuser")
    void isJpgImageNotNull() throws IOException {
        ByteArrayResource response = blackAndWhiteService.getBlackAndWhite("test@test.com",
                "test");

        BufferedImage bufferedImage = ImageIO.read(response.getInputStream());
        if (bufferedImage == null) {
            fail();
        }
    }

    @Test
    @WithMockUser(username = "testuser")
    void isPngDataArraySameLength() throws IOException {
        ByteArrayResource response = blackAndWhiteService.getBlackAndWhite("test@test.com",
                "test1");

        BufferedImage bufferedImage = ImageIO.read(response.getInputStream());
        byte[] colorValues = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        byte[] colorValuesBefore = ((DataBufferByte) this.bufferedImagePng.getRaster().getDataBuffer()).getData();
        if (colorValues.length != colorValuesBefore.length) {
            fail();
        }
    }
    @Test
    @WithMockUser(username = "testuser")
    void arePngDimensionsTheSame() throws IOException {
        ByteArrayResource response = blackAndWhiteService.getBlackAndWhite("test@test.com",
                "test1");

        BufferedImage bufferedImage = ImageIO.read(response.getInputStream());
        if (bufferedImage.getHeight() != this.bufferedImagePng.getHeight() ||
                bufferedImage.getWidth() != this.bufferedImagePng.getWidth()) {
            fail();
        }
    }
    @Test
    @WithMockUser(username = "testuser")
    void isPngImageNotNull() throws IOException {
        ByteArrayResource response = blackAndWhiteService.getBlackAndWhite("test@test.com",
                "test1");

        BufferedImage bufferedImage = ImageIO.read(response.getInputStream());
        if (bufferedImage == null) {
            fail();
        }
    }
}

