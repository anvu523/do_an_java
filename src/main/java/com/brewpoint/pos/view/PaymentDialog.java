package com.brewpoint.pos.view;

import com.brewpoint.pos.model.PaymentInput;
import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.util.FormLayout;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;
import com.brewpoint.pos.util.ValidationException;
import com.brewpoint.pos.util.ValidationUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;

public class PaymentDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final int DIALOG_WIDTH = 540;
    private static final int DETAILS_MIN_HEIGHT = 300;
    private static final BigDecimal[] QUICK_AMOUNTS = new BigDecimal[]{
            new BigDecimal("50000"),
            new BigDecimal("100000"),
            new BigDecimal("200000"),
            new BigDecimal("500000")
    };

    private final BigDecimal total;
    private final JRadioButton cashRadio = new JRadioButton("Tiền mặt", true);
    private final JRadioButton transferRadio = new JRadioButton("Chuyển khoản");
    private final JTextField receivedField = new JTextField(14);
    private final JLabel changeLabel = new JLabel("0 ₫", SwingConstants.RIGHT);
    private final JCheckBox transferConfirmedBox = new JCheckBox("Đã nhận đủ tiền chuyển khoản");
    private final JPanel cashPanel = new JPanel();
    private final JPanel transferPanel = new JPanel();
    private final JPanel paymentDetailsPanel = new JPanel(new CardLayout());
    private boolean suppressReceivedEvents;
    private PaymentInput paymentInput;

    public PaymentDialog(Window owner, BigDecimal total) {
        super(owner, "Thanh toán", Dialog.ModalityType.APPLICATION_MODAL);
        this.total = total;
        getContentPane().setBackground(UIConstants.BG_PANEL);
        setLayout(new BorderLayout());
        add(buildContent(), BorderLayout.CENTER);
        javax.swing.JButton cancelButton = UiUtils.secondaryButton("Hủy");
        cancelButton.addActionListener(e -> dispose());
        javax.swing.JButton confirmButton = UiUtils.primaryButton("Xác nhận");
        confirmButton.addActionListener(e -> accept());
        add(UiUtils.dialogFooter(cancelButton, confirmButton), BorderLayout.SOUTH);
        wirePaymentMethodToggle();
        setReceivedAmount(total);
        updatePaymentMethodUi();
        setMinimumSize(new Dimension(DIALOG_WIDTH, 460));
        pack();
        setLocationRelativeTo(owner);
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
        totalLabel.setBorder(new EmptyBorder(0, 0, UIConstants.SPACING_SM, 0));
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
        receivedField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                onReceivedChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                onReceivedChanged();
            }

            public void changedUpdate(DocumentEvent e) {
                onReceivedChanged();
            }
        });
        receivedField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                formatReceivedField();
            }
        });
        transferConfirmedBox.setFont(UIConstants.font(UIConstants.FONT_LABEL));

        changeLabel.setFont(UIConstants.fontBold(UIConstants.FONT_TOTAL));
        changeLabel.setForeground(UIConstants.PRIMARY);

        JPanel cashForm = new FormLayout()
                .addRow("Tiền khách đưa", receivedField)
                .addRow("Tiền thừa", changeLabel)
                .build();
        cashForm.setOpaque(false);
        cashForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        cashPanel.setLayout(new BoxLayout(cashPanel, BoxLayout.Y_AXIS));
        cashPanel.setOpaque(false);
        cashPanel.add(cashForm);
        cashPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        cashPanel.add(buildQuickAmountPanel());

        transferPanel.setLayout(new BoxLayout(transferPanel, BoxLayout.Y_AXIS));
        transferPanel.setOpaque(false);
        transferPanel.add(buildTransferGuidePanel());
        transferPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        JPanel confirmRow = new JPanel(new BorderLayout());
        confirmRow.setOpaque(false);
        confirmRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmRow.add(transferConfirmedBox, BorderLayout.WEST);
        transferPanel.add(confirmRow);

        paymentDetailsPanel.setOpaque(false);
        paymentDetailsPanel.setMinimumSize(new Dimension(DIALOG_WIDTH - 64, DETAILS_MIN_HEIGHT));
        paymentDetailsPanel.setPreferredSize(new Dimension(DIALOG_WIDTH - 64, DETAILS_MIN_HEIGHT));
        paymentDetailsPanel.add(cashPanel, "cash");
        paymentDetailsPanel.add(transferPanel, "transfer");

        JPanel body = new JPanel(new BorderLayout(0, UIConstants.SPACING_MD));
        body.setOpaque(false);
        body.add(methodPanel, BorderLayout.NORTH);
        body.add(paymentDetailsPanel, BorderLayout.CENTER);
        wrapper.add(body, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildTransferGuidePanel() {
        JPanel steps = new JPanel();
        steps.setLayout(new BoxLayout(steps, BoxLayout.Y_AXIS));
        steps.setOpaque(false);
        steps.setAlignmentX(Component.LEFT_ALIGNMENT);

        String amountText = MoneyUtils.formatVnd(total);
        steps.add(buildGuideStep("1", "Yêu cầu khách chuyển khoản qua số tài khoản hoặc mã QR tại quầy."));
        steps.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        steps.add(buildGuideStep("2", "Chụp ảnh bill chuyển tiền của khách để đối chiếu khi cần."));
        steps.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        steps.add(buildGuideStep("3", "Kiểm tra đã nhận đủ " + amountText
                + ", tick xác nhận bên dưới rồi bấm Xác nhận."));

        JPanel panel = UiUtils.sectionPanel("Hướng dẫn thu ngân", steps);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JPanel buildGuideStep(String stepNumber, String text) {
        JPanel row = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel badge = new JLabel(stepNumber, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(UIConstants.PRIMARY);
        badge.setForeground(Color.WHITE);
        badge.setFont(UIConstants.fontBold(16f));
        badge.setPreferredSize(new Dimension(30, 30));
        badge.setMinimumSize(new Dimension(30, 30));
        badge.setMaximumSize(new Dimension(30, 30));

        JTextArea message = createGuideText(text);

        row.add(badge, BorderLayout.WEST);
        row.add(message, BorderLayout.CENTER);
        return row;
    }

    private JTextArea createGuideText(String text) {
        JTextArea message = new JTextArea(text);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setEditable(false);
        message.setFocusable(false);
        message.setOpaque(false);
        message.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        message.setForeground(UIConstants.TEXT_PRIMARY);
        message.setBorder(null);
        int textWidth = DIALOG_WIDTH - 108;
        message.setSize(new Dimension(textWidth, Short.MAX_VALUE));
        int height = message.getPreferredSize().height;
        Dimension size = new Dimension(textWidth, height);
        message.setPreferredSize(size);
        message.setMinimumSize(size);
        message.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return message;
    }

    private JPanel buildQuickAmountPanel() {
        JPanel grid = new JPanel(new GridLayout(2, 3, UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        grid.setOpaque(false);
        for (int i = 0; i < QUICK_AMOUNTS.length; i++) {
            final BigDecimal amount = QUICK_AMOUNTS[i];
            javax.swing.JButton button = UiUtils.secondaryButton(MoneyUtils.formatVndInput(amount));
            button.addActionListener(e -> setReceivedAmount(amount));
            grid.add(button);
        }
        javax.swing.JButton exactButton = UiUtils.secondaryButton("Vừa đủ");
        exactButton.addActionListener(e -> setReceivedAmount(total));
        grid.add(exactButton);
        grid.add(new JPanel());

        JPanel panel = UiUtils.sectionPanel("Chọn nhanh", grid);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        return panel;
    }

    private void wirePaymentMethodToggle() {
        ActionListener listener = e -> updatePaymentMethodUi();
        cashRadio.addActionListener(listener);
        transferRadio.addActionListener(listener);
    }

    private void updatePaymentMethodUi() {
        CardLayout layout = (CardLayout) paymentDetailsPanel.getLayout();
        layout.show(paymentDetailsPanel, cashRadio.isSelected() ? "cash" : "transfer");
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void onReceivedChanged() {
        if (!suppressReceivedEvents) {
            updateChangePreview();
        }
    }

    private void setReceivedAmount(BigDecimal amount) {
        suppressReceivedEvents = true;
        receivedField.setText(MoneyUtils.formatVndInput(amount));
        suppressReceivedEvents = false;
        updateChangePreview();
    }

    private void formatReceivedField() {
        BigDecimal received = tryParseReceived();
        if (received == null) {
            return;
        }
        suppressReceivedEvents = true;
        receivedField.setText(MoneyUtils.formatVndInput(received));
        suppressReceivedEvents = false;
        updateChangePreview();
    }

    private void updateChangePreview() {
        if (!cashRadio.isSelected()) {
            return;
        }
        BigDecimal received = tryParseReceived();
        if (received == null) {
            changeLabel.setText("—");
            changeLabel.setForeground(UIConstants.TEXT_MUTED);
            return;
        }
        BigDecimal change = received.subtract(total);
        if (change.signum() < 0) {
            changeLabel.setText("Chưa đủ " + MoneyUtils.formatVnd(change.abs()));
            changeLabel.setForeground(UIConstants.DANGER);
            return;
        }
        changeLabel.setText(MoneyUtils.formatVnd(change));
        changeLabel.setForeground(UIConstants.PRIMARY);
    }

    private BigDecimal tryParseReceived() {
        String value = receivedField.getText();
        if (value == null) {
            return null;
        }
        String clean = value.trim().replace(".", "").replace(",", "");
        if (clean.isEmpty() || !clean.matches("\\d+")) {
            return null;
        }
        return new BigDecimal(clean);
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
            } else if (!transferConfirmedBox.isSelected()) {
                throw new ValidationException("Phải xác nhận đã nhận tiền chuyển khoản.");
            }
            paymentInput = new PaymentInput(method, received, transferConfirmedBox.isSelected());
            dispose();
        } catch (RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }
}
