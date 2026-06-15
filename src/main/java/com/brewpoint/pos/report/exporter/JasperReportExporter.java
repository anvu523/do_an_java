package com.brewpoint.pos.report.exporter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.swing.JRViewer;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class JasperReportExporter {

    public void preview(JasperPrint print, String title) {
        preview(print, title, null);
    }

    public void preview(JasperPrint print, String title, Component parent) {
        if (print == null) {
            throw new IllegalArgumentException("Không có dữ liệu báo cáo để xem trước.");
        }
        Runnable showViewer = new Runnable() {
            public void run() {
                String windowTitle = title;
                if (windowTitle == null || windowTitle.trim().length() == 0) {
                    windowTitle = "Xem trước";
                }
                final Window modalToRestore = findVisibleModalWindow(parent);
                final Window previewOwner = findFrameOwner(parent);
                if (modalToRestore != null) {
                    modalToRestore.setVisible(false);
                }

                Frame ownerFrame = previewOwner instanceof Frame ? (Frame) previewOwner : null;
                final JDialog previewDialog = new JDialog(ownerFrame, windowTitle, Dialog.ModalityType.APPLICATION_MODAL);
                JRViewer viewer = new JRViewer(print);
                JPanel root = new JPanel(new BorderLayout());
                root.add(viewer, BorderLayout.CENTER);
                root.add(buildCloseBar(previewDialog), BorderLayout.SOUTH);
                previewDialog.setContentPane(root);
                previewDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                previewDialog.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent e) {
                        showModalAgain(modalToRestore);
                    }
                });
                previewDialog.setSize(900, 700);
                previewDialog.setLocationRelativeTo(previewOwner);
                previewDialog.setVisible(true);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            showViewer.run();
        } else {
            SwingUtilities.invokeLater(showViewer);
        }
    }

    public void print(JasperPrint print) throws JRException {
        print(print, null);
    }

    /**
     * Hiện hộp thoại in Windows (chọn Microsoft Print to PDF hoặc máy in khác).
     */
    public void print(JasperPrint print, Component parent) throws JRException {
        if (print == null) {
            throw new IllegalArgumentException("Không có dữ liệu báo cáo để in.");
        }
        ensurePrintServiceAvailable();
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
        configuration.setDisplayPrintDialog(true);
        configuration.setDisplayPageDialog(false);
        exporter.setConfiguration(configuration);
        exporter.exportReport();
    }

    public void exportPdf(JasperPrint print, File targetFile) throws JRException {
        JasperExportManager.exportReportToPdfFile(print, targetFile.getAbsolutePath());
    }

    public void showPrintError(Component parent, Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "Không thể in báo cáo.";
        }
        JOptionPane.showMessageDialog(parent, message, "Lỗi in", JOptionPane.ERROR_MESSAGE);
    }

    public boolean hasPrintService() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        return services != null && services.length > 0;
    }

    private JPanel buildCloseBar(final JDialog previewDialog) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> previewDialog.dispose());
        bar.add(closeButton);
        return bar;
    }

    private void showModalAgain(Window modalToRestore) {
        if (modalToRestore == null) {
            return;
        }
        if (!modalToRestore.isVisible()) {
            modalToRestore.setVisible(true);
            modalToRestore.toFront();
            modalToRestore.requestFocus();
        }
    }

    /**
     * Dialog modal đang che (vd. CheckoutSuccessDialog) — ẩn trước khi xem hóa đơn.
     */
    private Window findVisibleModalWindow(Component parent) {
        Component current = parent;
        while (current != null) {
            if (current instanceof Dialog) {
                Dialog dialog = (Dialog) current;
                if (dialog.isModal() && dialog.isVisible()) {
                    return dialog;
                }
            }
            current = current.getParent();
        }
        return null;
    }

    /**
     * Frame gốc làm owner preview — tránh gắn vào dialog modal đang ẩn.
     */
    private Window findFrameOwner(Component parent) {
        Window window = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        while (window != null && !(window instanceof Frame)) {
            window = window.getOwner();
        }
        return window;
    }

    private void ensurePrintServiceAvailable() {
        if (!hasPrintService()) {
            throw new IllegalStateException(
                    "Không tìm thấy máy in trên Windows.\n\n"
                            + "Cài thêm máy in ảo:\n"
                            + "Cài đặt → Bluetooth và thiết bị → Máy in → Thêm máy in\n"
                            + "→ Microsoft Print to PDF\n\n"
                            + "Hoặc dùng nút Xuất PDF để demo.");
        }
    }
}
