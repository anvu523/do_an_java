package com.brewpoint.pos.report.exporter;

import com.brewpoint.pos.util.UiUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;

public class PdfExportService {
    private final JasperReportExporter jasperReportExporter;

    public PdfExportService() {
        this(new JasperReportExporter());
    }

    public PdfExportService(JasperReportExporter jasperReportExporter) {
        this.jasperReportExporter = jasperReportExporter;
    }

    public File exportWithChooser(Component parent, JasperPrint print, String defaultFileName) throws JRException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất PDF");
        chooser.setSelectedFile(new File(defaultFileName));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File target = resolvePdfTarget(chooser.getSelectedFile());
        if (target.exists() && !UiUtils.confirm(parent, "File đã tồn tại. Ghi đè?")) {
            return null;
        }
        exportToFile(print, target);
        return target;
    }

    public void exportToFile(JasperPrint print, File targetFile) throws JRException {
        if (print == null) {
            throw new IllegalArgumentException("Không có dữ liệu báo cáo để xuất.");
        }
        if (targetFile == null) {
            throw new IllegalArgumentException("Đường dẫn xuất PDF không hợp lệ.");
        }
        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Không tạo được thư mục xuất PDF.");
        }
        jasperReportExporter.exportPdf(print, targetFile);
    }

    public File resolvePdfTarget(File selectedFile) {
        if (selectedFile == null) {
            throw new IllegalArgumentException("Tên file không hợp lệ.");
        }
        if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
            return new File(selectedFile.getParentFile(), selectedFile.getName() + ".pdf");
        }
        return selectedFile;
    }

    public String sanitizePdfFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên file PDF không hợp lệ.");
        }
        String safe = fileName.trim().replaceAll("[\\\\/:*?\"<>|]", "_");
        if (!safe.toLowerCase().endsWith(".pdf")) {
            safe = safe + ".pdf";
        }
        return safe;
    }
}
