package com.pisti.harmonicrainbow.service.utility;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ImageNameConverter {
    public Map<String, String> convertImageNameToNameAndFormat(String imageNameWithFormat) {
        Map<String, String> result = new HashMap<>();
        List<String> image = new ArrayList<>(Arrays.asList(imageNameWithFormat.split("")));
        String name = "";
        String format = "";
        for (int i = image.size() - 1; i >= 0; i--) {
            if (image.get(i).equals(".")) {
                name = String.join("", image.subList(0, i));
                format = String.join("", image.subList(i + 1, image.size()));
            }
        }
        result.put("name", name);
        result.put("format",  format);
        return result;
    }
}
