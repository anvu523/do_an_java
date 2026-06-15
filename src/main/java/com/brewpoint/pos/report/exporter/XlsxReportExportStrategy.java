package com.brewpoint.pos.report.exporter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import java.io.File;

public class XlsxReportExportStrategy implements ReportExportStrategy {
    @Override
    public String getExtension() {
        return "xlsx";
    }

    @Override
    public String getDescription() {
        return "Excel (*.xlsx)";
    }

    @Override
    public void export(JasperPrint print, File targetFile) throws JRException {
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(targetFile));
        
        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        exporter.setConfiguration(configuration);
        
        exporter.exportReport();
    }
}
