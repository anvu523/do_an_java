package com.brewpoint.pos.report.service;

import com.brewpoint.pos.report.util.JasperTemplateLoader;
import com.brewpoint.pos.report.util.ReportTemplate;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractJasperReportService {

    protected JasperPrint fill(ReportTemplate template, Map<String, Object> parameters) throws JRException {
        JasperReport report = JasperTemplateLoader.compile(template.getFileName());
        return JasperFillManager.fillReport(report, parameters, new JREmptyDataSource(1));
    }

    protected JasperPrint fill(ReportTemplate template, Map<String, Object> parameters, Collection<?> rows) throws JRException {
        JasperReport report = JasperTemplateLoader.compile(template.getFileName());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(rows);
        return JasperFillManager.fillReport(report, parameters, dataSource);
    }
}
