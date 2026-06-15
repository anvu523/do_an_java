package com.brewpoint.pos.report.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

public final class JasperFontBootstrap {
    private static final String[] FONT_RESOURCES = new String[]{
            "/net/sf/jasperreports/fonts/dejavu/DejaVuSans.ttf",
            "/net/sf/jasperreports/fonts/dejavu/DejaVuSans-Bold.ttf",
            "/net/sf/jasperreports/fonts/dejavu/DejaVuSans-Oblique.ttf",
            "/net/sf/jasperreports/fonts/dejavu/DejaVuSans-BoldOblique.ttf"
    };

    private static boolean initialized;

    private JasperFontBootstrap() {
    }

    public static synchronized void ensureInitialized() {
        if (initialized) {
            return;
        }
        registerAwtFonts();
        initialized = true;
    }

    private static void registerAwtFonts() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (int i = 0; i < FONT_RESOURCES.length; i++) {
            registerFont(environment, FONT_RESOURCES[i]);
        }
    }

    private static void registerFont(GraphicsEnvironment environment, String resourcePath) {
        InputStream inputStream = JasperFontBootstrap.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            return;
        }
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            environment.registerFont(font);
        } catch (Exception ignored) {
            // Fallback: Jasper extension properties may still provide the font for export.
        } finally {
            try {
                inputStream.close();
            } catch (Exception ignored) {
                // ignore close errors
            }
        }
    }
}
