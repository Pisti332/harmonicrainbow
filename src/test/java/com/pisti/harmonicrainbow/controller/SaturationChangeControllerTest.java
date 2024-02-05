package com.pisti.harmonicrainbow.controller;

import com.pisti.harmonicrainbow.service.SaturationChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SaturationChangeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static String email;
    private static String name;
    private static int saturation;
    @MockBean
    private SaturationChangeService saturationChangeService;

    @BeforeEach
    void setup() {
        email = "pityugamepla@gmail.com";
        name = "testing";
        saturation = 90;
        byte[] bytes = new byte[]{(byte) 10, (byte) 10, (byte) 10};
        ByteArrayResource inputStream = new ByteArrayResource(bytes);

        when(saturationChangeService.changeSaturation(email, name, saturation)).thenReturn(inputStream);
    }
    @Test
    @WithMockUser(username = "testuser")
    void shouldReturn200IfEverythingIsFine() throws Exception {
        String url = "/api/service/saturation-change?name=" + name + "&email=" + email + "&saturation=" + saturation;
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturnImage() throws Exception {
        String url = "/api/service/saturation-change?name=" + name + "&email=" + email + "&saturation=" + saturation;
        mockMvc.perform(get(url))
                .andExpect(MockMvcResultMatchers.content().contentType("image/jpeg"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void isImageNotNull() throws Exception {
        String url = "/api/service/saturation-change?name=" + name + "&email=" + email + "&saturation=" + saturation;
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andReturn();
        byte[] imageContent = mvcResult.getResponse().getContentAsByteArray();
        assertTrue(imageContent.length > 0);
    }
    @Test
    void shouldReturn403IfNoTokenOrWrongToken() throws Exception {
        String url = "/api/service/saturation-change?name=" + name + "&email=" + email + "&saturation=" + saturation;
        mockMvc.perform(get(url))
                .andExpect(status().isForbidden());
    }
}
