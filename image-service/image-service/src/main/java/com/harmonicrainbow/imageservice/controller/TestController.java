package com.harmonicrainbow.imageservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("image/test")
public class TestController {
    @GetMapping
    public Map<String, String> test() {
        Map<String, String> test = new HashMap<>();
        test.put("Test image", "success");
        return test;
    }
}
