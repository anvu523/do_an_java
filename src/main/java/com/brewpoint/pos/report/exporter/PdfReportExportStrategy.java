package com.brewpoint.pos.report.exporter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import java.io.File;

public class PdfReportExportStrategy implements ReportExportStrategy {
    @Override
    public String getExtension() {
        return "pdf";
    }

    @Override
    public String getDescription() {
        return "PDF (*.pdf)";
    }

    @Override
    public void export(JasperPrint print, File targetFile) throws JRException {
        JasperExportManager.exportReportToPdfFile(print, targetFile.getAbsolutePath());
    }
}
