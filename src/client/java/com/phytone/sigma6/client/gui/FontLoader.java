package com.phytone.sigma6.client.gui;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class FontLoader {

    private static final Map<String, Font> FONT_CACHE =
            new HashMap<>();

    private FontLoader() {
    }

    public static Font load(String name, float size) {

        Font baseFont = FONT_CACHE.computeIfAbsent(
                name,
                FontLoader::loadFont
        );

        return baseFont.deriveFont(size);
    }

    private static Font loadFont(String name) {

        String path = "/assets/fonts/" + name + ".ttf";

        try (InputStream stream =
                     FontLoader.class.getResourceAsStream(path)) {

            if (stream == null) {
                throw new IllegalArgumentException(
                        "Font not found: " + path
                );
            }

            return Font.createFont(
                    Font.TRUETYPE_FONT,
                    stream
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load font: " + path,
                    e
            );
        }
    }
}