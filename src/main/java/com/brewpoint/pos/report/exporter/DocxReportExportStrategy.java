package com.brewpoint.pos.report.exporter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import java.io.File;

public class DocxReportExportStrategy implements ReportExportStrategy {
    @Override
    public String getExtension() {
        return "docx";
    }

    @Override
    public String getDescription() {
        return "Word Document (*.docx)";
    }

    @Override
    public void export(JasperPrint print, File targetFile) throws JRException {
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(targetFile));
        exporter.exportReport();
    }
}
