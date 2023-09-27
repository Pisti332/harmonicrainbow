package com.harmonicrainbow.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user/test")
public class TestController {
    @GetMapping
    public Map<String, String> ping() {
        Map<String, String> test = new HashMap<>();
        test.put("Test user", "success");
        return test;
    }
}
