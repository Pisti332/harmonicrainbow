package com.pisti.harmonicrainbow.service.utility;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HslAndRgbConverter {
    public Map<String, Float> convertRGBtoHSL(int r, int g, int b) {
        Map<String, Float> HSL = new HashMap<>();
        float h, s, l, rVal, gVal, bVal;

        rVal = (float) r / 255;
        gVal = (float) g / 255;
        bVal = (float) b / 255;

        float maxColor = Math.max(rVal, Math.max(gVal, bVal));
        float minColor = Math.min(rVal, Math.min(gVal, bVal));

        if ((rVal == gVal) && (gVal == bVal)) {
            h = 0;
            s = 0;
            l = rVal;
        } else {
            float d = maxColor - minColor;
            l = (minColor + maxColor) / 2;
            if (l < 0.5) {
                s = d / (maxColor + minColor);
            } else {
                s = d / (2 - maxColor - minColor);
            }
            if (rVal == maxColor) {
                h = (gVal - bVal) / (maxColor - minColor);
            } else if (gVal == maxColor) {
                h = 2 + (bVal - rVal) / (maxColor - minColor);
            } else {
                h = 4 + (rVal - gVal) / (maxColor - minColor);
            }
            h /= 6;
            if (h < 0) {
                h++;
            }
        }
        float hVal = h * 360;
        float sVal = s * 100;
        float lVal = l * 100;
        HSL.put("h", hVal);
        HSL.put("s", sVal);
        HSL.put("l", lVal);

        return HSL;
    }

    public Map<String, Integer> convertHSLtoRGB(float hVal, float sVal, float lVal) {
        Map<String, Integer> RGB = new HashMap<>();

        float r, g, b, h, s, l;
        float temp1, temp2, tempr, tempg, tempb;

        h = hVal / 360;
        s = sVal / 100;
        l = lVal / 100;

        if (s == 0) {
            r = l;
            g = l;
            b = l;
        } else {
            if (l < 0.5) temp2 = l * (1 + s);
            else {
                temp2 = (l + s) - (l * s);
            }
            temp1 = 2 * l - temp2;
            tempr = h + (float) 1 / 3;
            if (tempr > 1) {
                tempr--;
            }
            tempg = h;
            tempb = h - (float) 1 / 3;
            if (tempb < 0) {
                tempb++;
            }

            r = calcTemp(tempr, temp1, temp2);
            g = calcTemp(tempg, temp1, temp2);
            b = calcTemp(tempb, temp1, temp2);
        }

        int rVal = (int) (r * 255);
        int gVal = (int) (g * 255.0);
        int bVal = (int) (b * 255.0);

        RGB.put("r", rVal);
        RGB.put("g", gVal);
        RGB.put("b", bVal);

        return RGB;
    }
    private float calcTemp(float currentTemp, float temp1, float temp2) {
        float currentChannel;
        if (currentTemp < (float) 1 / 6) {
            currentChannel = temp1 + (temp2 - temp1) * 6 * currentTemp;
        } else if (currentTemp < 0.5) {
            currentChannel = temp2;
        } else if (currentTemp < (float) 2 / 3) {
            currentChannel = temp1 + (temp2 - temp1) * (((float) 2 / 3) - currentTemp) * 6;
        } else {
            currentChannel = temp1;
        }
        return currentChannel;
    }
}
