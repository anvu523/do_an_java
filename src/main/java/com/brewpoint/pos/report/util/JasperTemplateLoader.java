package com.brewpoint.pos.report.util;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRException;

import java.io.InputStream;

public final class JasperTemplateLoader {
    private static final String TEMPLATE_PREFIX = "/report/templates/";

    private JasperTemplateLoader() {
    }

    public static JasperReport compile(String templateFileName) throws JRException {
        JasperFontBootstrap.ensureInitialized();
        String path = TEMPLATE_PREFIX + templateFileName;
        InputStream inputStream = JasperTemplateLoader.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new IllegalStateException("Không tìm thấy mẫu báo cáo: " + templateFileName);
        }
        try {
            return JasperCompileManager.compileReport(inputStream);
        } finally {
            try {
                inputStream.close();
            } catch (Exception ignored) {
                // ignore close errors
            }
        }
    }
}
