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
public class SaturationChangeServiceTest {
    private SaturationChangeService saturationChangeService;
    private BufferedImage bufferedImageJpg;
    private BufferedImage bufferedImagePng;

    @Autowired
    public SaturationChangeServiceTest(SaturationChangeService saturationChangeService) {
        this.saturationChangeService = saturationChangeService;
    }

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
        when(imageService.getImageByEmailAndName("test@test.com", "test")).thenReturn(responseJpg);
        when(imageService.getImageByEmailAndName("test@test.com", "test1")).thenReturn(responsePng);
    }
    @Test
    @WithMockUser(username = "testuser")
    void isSaturationProperFor50Jpg() throws IOException {
        ResponseEntity<Object> response = saturationChangeService.changeSaturation("test@test.com",
                "test", 50);
        ByteArrayResource image = (ByteArrayResource) response.getBody();
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        byte[] colorValues = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        // TODO test array
        fail();
    }

    @Test
    @WithMockUser(username = "testuser")
    void isSaturationProperFor50Png() throws IOException {
        ResponseEntity<Object> response = saturationChangeService.changeSaturation("test@test.com",
                "test1", 50);
        ByteArrayResource image = (ByteArrayResource) response.getBody();
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        byte[] colorValues = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        //TODO test
        fail();
    }
}
