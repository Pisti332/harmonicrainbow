package com.pisti.harmonicrainbow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pisti.harmonicrainbow.service.ColorChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ColorChangeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private String email;
    private String name;
    private Map<String, Map<String, Integer>> body;

    @MockBean
    private ColorChangeService colorChangeService;

    @BeforeEach
    void setup() {
        email = "test@test.com";
        name = "test";
        body = new HashMap<>();
        byte[] bytes = new byte[]{(byte) 10, (byte) 10, (byte) 10};
        ByteArrayResource inputStream = new ByteArrayResource(bytes);

        Map<String, Integer> from = new HashMap<>();
        from.put("red", 0);
        from.put("green", 0);
        from.put("blue", 0);
        body.put("from", from);

        Map<String, Integer> to = new HashMap<>();
        to.put("red", 255);
        to.put("green", 255);
        to.put("blue", 255);
        body.put("to", to);

        Map<String, Integer> newColor = new HashMap<>();
        newColor.put("red", 0);
        newColor.put("green", 0);
        newColor.put("blue", 0);
        body.put("newColor", newColor);
        when(colorChangeService.changeColors(body, email, name)).thenReturn(inputStream);

    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturn200IfEverythingIsFine() throws Exception {
        String url = "/api/service/change-color?name=" + name + "&email=" + email;
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(body);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturnImage() throws Exception {
        String url = "/api/service/change-color?name=" + name + "&email=" + email;
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(body);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(MockMvcResultMatchers.content().contentType("image/jpeg"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void isImageNotNull() throws Exception {
        String url = "/api/service/change-color?name=" + name + "&email=" + email;
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(body);
        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andReturn();
        byte[] imageContent = mvcResult.getResponse().getContentAsByteArray();
        assertTrue(imageContent.length > 0);
    }

    @Test
    void shouldReturn403IfNoTokenOrWrongToken() throws Exception {
        String url = "/api/service/change-color?name=" + name + "&email=" + email;
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(body);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isForbidden());
    }

}
