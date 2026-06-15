package com.brewpoint.pos.report.exporter;

import com.brewpoint.pos.util.UiUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;

public class ReportExportService {

    public File exportWithChooser(Component parent, JasperPrint print, String defaultFileName, ReportExportStrategy strategy) throws JRException {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy xuất báo cáo không được để trống.");
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất báo cáo " + strategy.getDescription());
        String sanitizedDefault = sanitizeFileName(defaultFileName, strategy.getExtension());
        chooser.setSelectedFile(new File(sanitizedDefault));
        chooser.setFileFilter(new FileNameExtensionFilter(strategy.getDescription(), strategy.getExtension()));
        
        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File target = resolveTarget(chooser.getSelectedFile(), strategy.getExtension());
        if (target.exists() && !UiUtils.confirm(parent, "File đã tồn tại. Bạn có muốn ghi đè?")) {
            return null;
        }
        exportToFile(print, target, strategy);
        return target;
    }

    public void exportToFile(JasperPrint print, File targetFile, ReportExportStrategy strategy) throws JRException {
        if (print == null) {
            throw new IllegalArgumentException("Không có dữ liệu báo cáo để xuất.");
        }
        if (targetFile == null) {
            throw new IllegalArgumentException("Đường dẫn xuất file không hợp lệ.");
        }
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy xuất báo cáo không được để trống.");
        }
        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Không tạo được thư mục xuất file.");
        }
        strategy.export(print, targetFile);
    }

    public File resolveTarget(File selectedFile, String extension) {
        if (selectedFile == null) {
            throw new IllegalArgumentException("Tên file không hợp lệ.");
        }
        String lowerExt = "." + extension.toLowerCase();
        String name = selectedFile.getName();
        // Thay đổi đuôi file tự động nếu người dùng chọn sai định dạng
        if (name.toLowerCase().endsWith(".pdf") && !"pdf".equalsIgnoreCase(extension)) {
            name = name.substring(0, name.length() - 4) + lowerExt;
            return new File(selectedFile.getParentFile(), name);
        } else if (name.toLowerCase().endsWith(".xlsx") && !"xlsx".equalsIgnoreCase(extension)) {
            name = name.substring(0, name.length() - 5) + lowerExt;
            return new File(selectedFile.getParentFile(), name);
        } else if (name.toLowerCase().endsWith(".docx") && !"docx".equalsIgnoreCase(extension)) {
            name = name.substring(0, name.length() - 5) + lowerExt;
            return new File(selectedFile.getParentFile(), name);
        }
        
        if (!name.toLowerCase().endsWith(lowerExt)) {
            return new File(selectedFile.getParentFile(), name + lowerExt);
        }
        return selectedFile;
    }

    public String sanitizeFileName(String fileName, String extension) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên file không hợp lệ.");
        }
        String cleanName = fileName.trim();
        // Loại bỏ đuôi cũ nếu trùng lặp
        if (cleanName.toLowerCase().endsWith(".pdf")) {
            cleanName = cleanName.substring(0, cleanName.length() - 4);
        } else if (cleanName.toLowerCase().endsWith(".xlsx")) {
            cleanName = cleanName.substring(0, cleanName.length() - 5);
        } else if (cleanName.toLowerCase().endsWith(".docx")) {
            cleanName = cleanName.substring(0, cleanName.length() - 5);
        }
        
        String safe = cleanName.replaceAll("[\\\\/:*?\"<>|]", "_");
        String lowerExt = "." + extension.toLowerCase();
        return safe + lowerExt;
    }
}
