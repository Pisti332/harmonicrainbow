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

    public Map<String, Integer> convertHSLtoRGB(int hVal, int sVal, int lVal) {
        Map<String, Integer> RGB = new HashMap<>();

        float r, g, b, h, s, l;
        float temp1, temp2, tempr, tempg, tempb;

        h = (float) (hVal % 260) / 360;
        s = (float) sVal / 100;
        l = (float) lVal / 100;

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

            if (tempr < 1.0 / 6.0) {
                r = temp1 + (temp2 - temp1) * 6 * tempr;
            } else if (tempr < 0.5) {
                r = temp2;
            } else if (tempr < 2.0 / 3.0) {
                r = temp1 + (temp2 - temp1) * (((float) 2 / 3) - tempr) * 6;
            } else {
                r = temp1;
            }

            if (tempg < (float) 1 / 6) {
                g = temp1 + (temp2 - temp1) * 6 * tempg;
            } else if (tempg < 0.5) {
                g = temp2;
            } else if (tempg < (float) 2 / 3) {
                g = temp1 + (temp2 - temp1) * (((float) 2 / 3) - tempg) * 6;
            } else {
                g = temp1;
            }

            if (tempb < (float) 1 / 6) {
                b = temp1 + (temp2 - temp1) * 6 * tempb;
            } else if (tempb < 0.5) {
                b = temp2;
            } else if (tempb < (float) 2 / 3) {
                b = temp1 + (temp2 - temp1) * (((float) 2 / 3) - tempb) * 6;
            } else {
                b = temp1;
            }
        }

        int rVal = (int) (r * 255);
        int gVal = (int) (g * 255.0);
        int bVal = (int) (b * 255.0);

        RGB.put("r", rVal);
        RGB.put("g", gVal);
        RGB.put("b", bVal);

        return RGB;
    }
}
