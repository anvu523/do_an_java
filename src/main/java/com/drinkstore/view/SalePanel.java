package com.drinkstore.view;

import com.drinkstore.controller.ProductController;
import com.drinkstore.controller.SaleController;
import com.drinkstore.model.OrderDetail;
import com.drinkstore.model.Product;
import com.drinkstore.util.UiUtil;
import com.drinkstore.util.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient ProductController productController = new ProductController();
    private final transient SaleController saleController = new SaleController();
    private final Integer employeeId;
    private final DefaultTableModel productTableModel = new DefaultTableModel(
            new Object[]{"Mã SP", "Tên sản phẩm", "Loại", "Giá", "Tồn"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel cartTableModel = new DefaultTableModel(
            new Object[]{"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable productTable = new JTable(productTableModel);
    private final JTable cartTable = new JTable(cartTableModel);
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JLabel totalLabel = new JLabel("Tổng tiền: 0 đ");
    private final transient List<Product> currentProducts = new ArrayList<>();
    private final transient List<OrderDetail> cartItems = new ArrayList<>();

    public SalePanel(Integer employeeId) {
        this.employeeId = employeeId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(buildProductPanel(), BorderLayout.WEST);
        add(buildCartPanel(), BorderLayout.CENTER);
        loadProducts();
    }

    private JPanel buildProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Sản phẩm đang bán"));
        UiUtil.configureTable(productTable);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.add(new JLabel("Số lượng"));
        controls.add(quantitySpinner);
        JButton addButton = new JButton("Thêm vào giỏ");
        addButton.addActionListener(e -> addToCart());
        JButton refreshButton = new JButton("Tải lại");
        refreshButton.addActionListener(e -> loadProducts());
        controls.add(addButton);
        controls.add(refreshButton);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Giỏ hàng"));
        UiUtil.configureTable(cartTable);
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        JButton removeButton = new JButton("Xóa khỏi giỏ");
        removeButton.addActionListener(e -> removeFromCart());
        JButton clearButton = new JButton("Xóa giỏ");
        clearButton.addActionListener(e -> clearCart());
        JButton checkoutButton = new JButton("Lưu hóa đơn");
        checkoutButton.addActionListener(e -> checkout());
        buttons.add(removeButton);
        buttons.add(clearButton);
        buttons.add(checkoutButton);
        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void loadProducts() {
        try {
            currentProducts.clear();
            currentProducts.addAll(productController.findActive());
            productTableModel.setRowCount(0);
            for (Product product : currentProducts) {
                productTableModel.addRow(new Object[]{
                        product.getProductId(),
                        product.getName(),
                        product.getCategoryName(),
                        UiUtil.money(product.getPrice()),
                        product.getStockQuantity()
                });
            }
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            UiUtil.showInfo(this, "Chọn sản phẩm cần bán.");
            return;
        }
        Product product = currentProducts.get(productTable.convertRowIndexToModel(row));
        int quantity = (Integer) quantitySpinner.getValue();
        int existingQuantity = cartItems.stream()
                .filter(item -> item.getProductId() == product.getProductId())
                .mapToInt(OrderDetail::getQuantity)
                .sum();
        if (quantity + existingQuantity > product.getStockQuantity()) {
            UiUtil.showError(this, new ValidationException("Không được bán vượt quá tồn kho."));
            return;
        }
        OrderDetail existing = cartItems.stream()
                .filter(item -> item.getProductId() == product.getProductId())
                .findFirst()
                .orElse(null);
        if (existing == null) {
            OrderDetail detail = new OrderDetail();
            detail.setProductId(product.getProductId());
            detail.setProductName(product.getName());
            detail.setQuantity(quantity);
            detail.setUnitPrice(product.getPrice());
            detail.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItems.add(detail);
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setLineTotal(existing.getUnitPrice().multiply(BigDecimal.valueOf(existing.getQuantity())));
        }
        fillCart();
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            UiUtil.showInfo(this, "Chọn sản phẩm trong giỏ cần xóa.");
            return;
        }
        cartItems.remove(cartTable.convertRowIndexToModel(row));
        fillCart();
    }

    private void checkout() {
        if (employeeId == null || employeeId <= 0) {
            UiUtil.showError(this, new ValidationException("Tài khoản chưa gắn với nhân viên."));
            return;
        }
        try {
            int orderId = saleController.checkout(employeeId, new ArrayList<>(cartItems));
            UiUtil.showInfo(this, "Đã lưu hóa đơn mã " + orderId + ".");
            clearCart();
            loadProducts();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void fillCart() {
        cartTableModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail item : cartItems) {
            cartTableModel.addRow(new Object[]{
                    item.getProductId(),
                    item.getProductName(),
                    item.getQuantity(),
                    UiUtil.money(item.getUnitPrice()),
                    UiUtil.money(item.getLineTotal())
            });
            total = total.add(item.getLineTotal());
        }
        totalLabel.setText("Tổng tiền: " + UiUtil.money(total));
    }

    private void clearCart() {
        cartItems.clear();
        fillCart();
    }
}
