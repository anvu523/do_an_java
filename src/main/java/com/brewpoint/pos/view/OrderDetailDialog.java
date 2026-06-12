package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.OrderController;
import com.brewpoint.pos.model.OrderItemDetail;
import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.model.ToppingSnapshot;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dialog;
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
        setSize(860, 440);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        UiUtils.configureTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel info = new JPanel();
        info.add(new javax.swing.JLabel("Tổng: " + MoneyUtils.formatVnd(order.getTotalAmount())));
        info.add(new javax.swing.JLabel("Thanh toán: " + order.getPaymentMethod().getDisplayName()));
        if (order.getReceivedAmount() != null) {
            info.add(new javax.swing.JLabel("Khách đưa: " + MoneyUtils.formatVnd(order.getReceivedAmount())));
        }
        if (order.getChangeAmount() != null) {
            info.add(new javax.swing.JLabel("Tiền thừa: " + MoneyUtils.formatVnd(order.getChangeAmount())));
        }
        add(info, BorderLayout.NORTH);
        loadData(order, controller);
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
