package com.brewpoint.pos.view;

import com.brewpoint.pos.model.PaymentInput;
import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UiUtils;
import com.brewpoint.pos.util.ValidationException;
import com.brewpoint.pos.util.ValidationUtils;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.math.BigDecimal;

public class PaymentDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final BigDecimal total;
    private final JRadioButton cashRadio = new JRadioButton("Tiền mặt", true);
    private final JRadioButton transferRadio = new JRadioButton("Chuyển khoản");
    private final JTextField receivedField = new JTextField(14);
    private final JCheckBox transferConfirmedBox = new JCheckBox("Tôi xác nhận đã nhận tiền");
    private PaymentInput paymentInput;

    public PaymentDialog(Window owner, BigDecimal total) {
        super(owner, "Thanh toán", Dialog.ModalityType.APPLICATION_MODAL);
        this.total = total;
        setSize(420, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        add(buildContent(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
    }

    public PaymentInput getPaymentInput() {
        return paymentInput;
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        JLabel totalLabel = new JLabel("Tổng tiền: " + MoneyUtils.formatVnd(total));
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 30f));
        panel.add(totalLabel);
        ButtonGroup group = new ButtonGroup();
        group.add(cashRadio);
        group.add(transferRadio);
        panel.add(cashRadio);
        panel.add(transferRadio);
        panel.add(new JLabel("Tiền khách đưa"));
        receivedField.setText(total.toPlainString());
        panel.add(receivedField);
        panel.add(transferConfirmedBox);
        return panel;
    }

    private JPanel buildBottom() {
        JPanel bottom = new JPanel();
        javax.swing.JButton cancelButton = new javax.swing.JButton("Hủy");
        cancelButton.addActionListener(e -> dispose());
        javax.swing.JButton confirmButton = UiUtils.primaryButton("Xác nhận");
        confirmButton.addActionListener(e -> accept());
        bottom.add(cancelButton);
        bottom.add(confirmButton);
        return bottom;
    }

    private void accept() {
        try {
            PaymentMethod method = cashRadio.isSelected() ? PaymentMethod.CASH : PaymentMethod.MANUAL_BANK_TRANSFER;
            BigDecimal received = null;
            if (method == PaymentMethod.CASH) {
                received = ValidationUtils.parseNonNegativeMoney(receivedField.getText(), "Tiền khách đưa");
                if (received.compareTo(total) < 0) {
                    throw new ValidationException("Số tiền khách đưa chưa đủ.");
                }
            }
            paymentInput = new PaymentInput(method, received, transferConfirmedBox.isSelected());
            dispose();
        } catch (RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }
}
