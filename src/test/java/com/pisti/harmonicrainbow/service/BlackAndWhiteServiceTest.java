package com.pisti.harmonicrainbow.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class BlackAndWhiteServiceTest {
//    @Test
//    @WithMockUser(username = "testuser")
//    void isImageBlackAndWhite() throws Exception {
//        String url = "/api/service/black-and-white?name=" + name + "&email=" + email;
//        MvcResult mvcResult = mockMvc.perform(post(url))
//                .andReturn();
//        byte[] imageContent = mvcResult.getResponse().getContentAsByteArray();
//        InputStream is = new ByteArrayInputStream(imageContent);
//        BufferedImage newBi = ImageIO.read(is);
//        byte[] pixels = ((DataBufferByte) newBi.getRaster().getDataBuffer()).getData();
//        if (newBi.getType() == BufferedImage.TYPE_INT_ARGB ||
//                newBi.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
//            for (int i = 0; i < pixels.length; i += 4) {
//                byte f = pixels[i + 3];
//                byte s = pixels[i + 2];
//                byte t = pixels[i + 1];
//                if (f != s || f != t) {
//                    fail();
//                }
//            }
//        } else if (newBi.getType() == BufferedImage.TYPE_3BYTE_BGR ||
//                newBi.getType() == BufferedImage.TYPE_INT_RGB) {
//            for (int i = 0; i < pixels.length; i += 3) {
//                byte f = pixels[i];
//                byte s = pixels[i + 1];
//                byte t = pixels[i + 2];
//                if (f != s || f != t) {
//                    fail();
//                }
//            }
//        }
//
//    }
}
