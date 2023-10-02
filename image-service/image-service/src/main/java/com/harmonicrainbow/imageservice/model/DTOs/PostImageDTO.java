package com.harmonicrainbow.imageservice.model.DTOs;

import com.harmonicrainbow.imageservice.model.ImageFormat;

import java.util.Base64;

public record PostImageDTO(String imageData, String imageName, String imageFormat, String email) {
}
