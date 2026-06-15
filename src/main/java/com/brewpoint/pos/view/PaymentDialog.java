package com.brewpoint.pos.view;

import com.brewpoint.pos.model.PaymentInput;
import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.util.FormLayout;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UIConstants;
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
        setSize(480, 380);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(UIConstants.BG_PANEL);
        setLayout(new BorderLayout());
        add(buildContent(), BorderLayout.CENTER);
        javax.swing.JButton cancelButton = UiUtils.secondaryButton("Hủy");
        cancelButton.addActionListener(e -> dispose());
        javax.swing.JButton confirmButton = UiUtils.primaryButton("Xác nhận");
        confirmButton.addActionListener(e -> accept());
        add(UiUtils.dialogFooter(cancelButton, confirmButton), BorderLayout.SOUTH);
    }

    public PaymentInput getPaymentInput() {
        return paymentInput;
    }

    private JPanel buildContent() {
        JPanel wrapper = new JPanel(new BorderLayout());
        UiUtils.styleDialogContent(wrapper);

        JLabel totalLabel = new JLabel("Tổng tiền: " + MoneyUtils.formatVnd(total));
        totalLabel.setFont(UIConstants.fontBold(UIConstants.FONT_TOTAL));
        totalLabel.setForeground(UIConstants.PRIMARY);
        wrapper.add(totalLabel, BorderLayout.NORTH);

        JPanel methodPanel = new JPanel(new GridLayout(1, 2, UIConstants.SPACING_SM, 0));
        methodPanel.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(cashRadio);
        group.add(transferRadio);
        cashRadio.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        transferRadio.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        methodPanel.add(cashRadio);
        methodPanel.add(transferRadio);

        UiUtils.styleField(receivedField);
        receivedField.setText(total.toPlainString());
        transferConfirmedBox.setFont(UIConstants.font(UIConstants.FONT_LABEL));

        JPanel form = new FormLayout()
                .addFullWidth(methodPanel)
                .addRow("Tiền khách đưa", receivedField)
                .addFullWidth(transferConfirmedBox)
                .build();
        form.setOpaque(false);
        wrapper.add(form, BorderLayout.CENTER);
        return wrapper;
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
