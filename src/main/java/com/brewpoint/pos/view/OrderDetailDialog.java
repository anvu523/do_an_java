package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.OrderController;
import com.brewpoint.pos.model.OrderItemDetail;
import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.model.ToppingSnapshot;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Món", "Size", "Topping", "Ghi chú", "SL", "Đơn giá", "Thành tiền"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public OrderDetailDialog(Window owner, OrderSummary order, OrderController controller) {
        super(owner, "Chi tiết " + order.getOrderCode(), Dialog.ModalityType.APPLICATION_MODAL);
        setSize(960, 500);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(UIConstants.BG_PANEL);
        setLayout(new BorderLayout());
        JPanel content = new JPanel(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        UiUtils.styleDialogContent(content);
        UiUtils.configureTable(table);
        content.add(buildInfoPanel(order), BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
        JButton closeButton = UiUtils.secondaryButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        add(UiUtils.dialogFooter(null, closeButton), BorderLayout.SOUTH);
        loadData(order, controller);
    }

    private JPanel buildInfoPanel(OrderSummary order) {
        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_LG, UIConstants.SPACING_SM));
        info.setOpaque(false);
        info.add(buildInfoLabel("Tổng: " + MoneyUtils.formatVnd(order.getTotalAmount()), true));
        info.add(buildInfoLabel("Thanh toán: " + order.getPaymentMethod().getDisplayName(), false));
        if (order.getReceivedAmount() != null) {
            info.add(buildInfoLabel("Khách đưa: " + MoneyUtils.formatVnd(order.getReceivedAmount()), false));
        }
        if (order.getChangeAmount() != null) {
            info.add(buildInfoLabel("Tiền thừa: " + MoneyUtils.formatVnd(order.getChangeAmount()), false));
        }
        return info;
    }

    private JLabel buildInfoLabel(String text, boolean emphasize) {
        JLabel label = new JLabel(text);
        if (emphasize) {
            label.setFont(UIConstants.fontBold(UIConstants.FONT_SECTION_TITLE));
            label.setForeground(UIConstants.PRIMARY);
        } else {
            label.setFont(UIConstants.font(UIConstants.FONT_LABEL));
            label.setForeground(UIConstants.TEXT_PRIMARY);
        }
        return label;
    }

    private void loadData(OrderSummary order, OrderController controller) {
        try {
            List<OrderItemDetail> details = controller.findDetails(order.getOrderId());
            model.setRowCount(0);
            for (OrderItemDetail detail : details) {
                model.addRow(new Object[]{
                        detail.getProductName(),
                        detail.getSizeName(),
                        toppingText(detail),
                        detail.getNote(),
                        Integer.valueOf(detail.getQuantity()),
                        MoneyUtils.formatVnd(detail.getUnitPrice()),
                        MoneyUtils.formatVnd(detail.getLineTotal())
                });
            }
        } catch (SQLException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private String toppingText(OrderItemDetail detail) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < detail.getToppings().size(); i++) {
            ToppingSnapshot topping = detail.getToppings().get(i);
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(topping.getName()).append(" +").append(MoneyUtils.formatVnd(topping.getExtraPrice()));
        }
        return builder.toString();
    }
}
