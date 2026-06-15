package com.brewpoint.pos.report.exporter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import java.io.File;

public interface ReportExportStrategy {
    String getExtension();
    String getDescription();
    void export(JasperPrint print, File targetFile) throws JRException;
}
