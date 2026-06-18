package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.ReportController;
import com.brewpoint.pos.model.CheckoutResult;
import com.brewpoint.pos.model.PaymentInput;
import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;

public class CheckoutSuccessDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final int DIALOG_WIDTH = 560;

    private final long orderId;
    private final Runnable onNewOrder;
    private final ReportController reportController;
    private boolean newOrderHandled;

    public CheckoutSuccessDialog(Window owner, CheckoutResult result, PaymentInput paymentInput,
            ReportController reportController, Runnable onNewOrder) {
        super(owner, "Thanh toán thành công", Dialog.ModalityType.APPLICATION_MODAL);
        this.orderId = result.getOrderId();
        this.reportController = reportController;
        this.onNewOrder = onNewOrder;
        getContentPane().setBackground(UIConstants.BG_PANEL);
        setLayout(new BorderLayout());
        add(buildContent(result, paymentInput), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                handleNewOrder();
            }
        });
        setMinimumSize(new Dimension(DIALOG_WIDTH, 0));
        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel buildContent(CheckoutResult result, PaymentInput paymentInput) {
        JPanel wrapper = new JPanel(new BorderLayout(0, UIConstants.SPACING_MD));
        UiUtils.styleDialogContent(wrapper);

        JLabel title = new JLabel("THANH TOÁN THÀNH CÔNG", SwingConstants.CENTER);
        title.setFont(UIConstants.fontBold(UIConstants.FONT_SECTION_TITLE));
        title.setForeground(UIConstants.PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, UIConstants.SPACING_SM, 0));
        wrapper.add(title, BorderLayout.NORTH);

        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setOpaque(false);
        summary.add(buildSummaryRow("Mã hóa đơn", result.getOrderCode(), false));
        summary.add(buildSummaryRow("Tổng thanh toán", MoneyUtils.formatVnd(result.getTotalAmount()), true));

        PaymentMethod method = paymentInput != null ? paymentInput.getMethod() : null;
        if (method == PaymentMethod.CASH) {
            BigDecimal received = paymentInput.getReceivedAmount();
            if (received != null) {
                summary.add(buildSummaryRow("Tiền khách đưa", MoneyUtils.formatVnd(received), false));
            }
            summary.add(buildSummaryRow("Tiền thừa", MoneyUtils.formatVnd(result.getChangeAmount()), true));
        } else {
            String methodText = method != null ? method.getDisplayName() : "—";
            summary.add(buildSummaryRow("Phương thức", methodText, false));
        }

        wrapper.add(summary, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildSummaryRow(String labelText, String valueText, boolean emphasizeValue) {
        JPanel row = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        row.setOpaque(false);
        row.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        label.setForeground(UIConstants.TEXT_MUTED);

        JLabel value = new JLabel(valueText, SwingConstants.RIGHT);
        if (emphasizeValue) {
            value.setFont(UIConstants.fontBold(UIConstants.FONT_LABEL));
            value.setForeground(UIConstants.PRIMARY);
        } else {
            value.setFont(UIConstants.font(UIConstants.FONT_LABEL));
            value.setForeground(UIConstants.TEXT_PRIMARY);
        }

        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        JPanel padded = new JPanel(new BorderLayout());
        padded.setOpaque(false);
        padded.setBorder(new EmptyBorder(4, 0, 4, 0));
        padded.add(row, BorderLayout.CENTER);
        return padded;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        footer.setBackground(UIConstants.BG_PANEL);
        footer.setBorder(new EmptyBorder(UIConstants.SPACING_SM, UIConstants.SPACING_LG,
                UIConstants.SPACING_MD, UIConstants.SPACING_LG));

        JButton newOrderButton = UiUtils.primaryButton("Đơn mới");
        newOrderButton.addActionListener(e -> dispose());

        JButton previewButton = UiUtils.secondaryButton("Xem hóa đơn");
        previewButton.addActionListener(e -> reportController.previewReceipt(orderId, this));

        JButton exportButton = UiUtils.secondaryButton("Xuất PDF");
        exportButton.addActionListener(e -> reportController.exportReceiptPdf(orderId, this));

        JButton printButton = UiUtils.secondaryButton("In hóa đơn");
        printButton.addActionListener(e -> reportController.printReceipt(orderId, this));

        footer.add(newOrderButton);
        footer.add(previewButton);
        footer.add(exportButton);
        footer.add(printButton);
        return footer;
    }

    private void handleNewOrder() {
        if (newOrderHandled) {
            return;
        }
        newOrderHandled = true;
        if (onNewOrder != null) {
            onNewOrder.run();
        }
    }
}
