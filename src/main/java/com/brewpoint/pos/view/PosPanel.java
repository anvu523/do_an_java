package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.controller.CheckoutController;
import com.brewpoint.pos.model.CartLine;
import com.brewpoint.pos.model.CartLineRequest;
import com.brewpoint.pos.model.Category;
import com.brewpoint.pos.model.CheckoutRequest;
import com.brewpoint.pos.model.CheckoutResult;
import com.brewpoint.pos.model.PaymentInput;
import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.model.ToppingSnapshot;
import com.brewpoint.pos.util.ImageService;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UiUtils;
import com.brewpoint.pos.util.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient CatalogController catalogController = new CatalogController();
    private final transient CheckoutController checkoutController = new CheckoutController();
    private final transient ImageService imageService = new ImageService();
    private final Integer employeeId;
    private final JComboBox<Category> categoryCombo = new JComboBox<Category>();
    private final JTextField searchField = new JTextField(22);
    private final JPanel productGrid = new JPanel(new GridLayout(0, 3, 12, 12));
    private final DefaultTableModel cartModel = new DefaultTableModel(
            new Object[]{"Món", "Tùy chọn", "SL", "Đơn giá", "Thành tiền"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable cartTable = new JTable(cartModel);
    private final JLabel totalLabel = new JLabel("0 ₫", SwingConstants.RIGHT);
    private final transient List<CartLine> cartLines = new ArrayList<CartLine>();
    private boolean processingCheckout;

    public PosPanel(Integer employeeId) {
        this.employeeId = employeeId;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(buildCatalogPanel(), BorderLayout.CENTER);
        add(buildCartPanel(), BorderLayout.EAST);
        loadCategories();
        loadProducts();
    }

    private JPanel buildCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Danh mục"));
        top.add(categoryCombo);
        top.add(new JLabel("Tìm"));
        top.add(searchField);
        JButton searchButton = new JButton("Tìm");
        searchButton.addActionListener(e -> loadProducts());
        JButton reloadButton = new JButton("Tải lại");
        reloadButton.addActionListener(e -> loadProducts());
        top.add(searchButton);
        top.add(reloadButton);
        panel.add(top, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(productGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(470, 0));
        UiUtils.panelBorder(panel, "Giỏ hàng");
        UiUtils.configureTable(cartTable);
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 28f));
        bottom.add(totalLabel, BorderLayout.NORTH);
        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        JButton removeButton = new JButton("Xóa dòng");
        removeButton.addActionListener(e -> removeSelectedLine());
        JButton clearButton = new JButton("Xóa giỏ");
        clearButton.addActionListener(e -> clearCartWithConfirm());
        JButton checkoutButton = UiUtils.primaryButton("Thanh toán");
        checkoutButton.addActionListener(e -> checkout());
        buttons.add(removeButton);
        buttons.add(clearButton);
        buttons.add(checkoutButton);
        bottom.add(buttons, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void loadCategories() {
        try {
            categoryCombo.removeAllItems();
            categoryCombo.addItem(new Category(0, "Tất cả", 0, true));
            List<Category> categories = catalogController.findCategories(true);
            for (Category category : categories) {
                categoryCombo.addItem(category);
            }
        } catch (SQLException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void loadProducts() {
        Category selected = (Category) categoryCombo.getSelectedItem();
        Integer categoryId = selected == null || selected.getCategoryId() == 0 ? null : Integer.valueOf(selected.getCategoryId());
        String keyword = searchField.getText();
        productGrid.removeAll();
        productGrid.add(new JLabel("Đang tải sản phẩm..."));
        productGrid.revalidate();
        productGrid.repaint();
        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            protected List<Product> doInBackground() throws Exception {
                return catalogController.searchProducts(keyword, categoryId, true);
            }

            protected void done() {
                try {
                    fillProducts(get());
                } catch (Exception ex) {
                    UiUtils.showError(PosPanel.this, new RuntimeException("Không tải được sản phẩm."));
                }
            }
        };
        worker.execute();
    }

    private void fillProducts(List<Product> products) {
        productGrid.removeAll();
        if (products == null || products.isEmpty()) {
            productGrid.add(new JLabel("Không tìm thấy sản phẩm phù hợp."));
        } else {
            for (Product product : products) {
                productGrid.add(createProductCard(product));
            }
        }
        productGrid.revalidate();
        productGrid.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setPreferredSize(new Dimension(210, 245));
        card.setBorder(BorderFactory.createLineBorder(UiUtils.BORDER));
        card.setBackground(Color.WHITE);
        JLabel imageLabel = new JLabel(imageService.loadThumbnail(product.getImagePath(), 200, 150));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(imageLabel, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setOpaque(false);
        JLabel nameLabel = new JLabel("<html><b>" + product.getName() + "</b></html>");
        JLabel priceLabel = new JLabel("Từ " + MoneyUtils.formatVnd(product.getFromPrice()));
        String stockText = product.getStockQuantity() > 0 ? "Còn " + product.getStockQuantity() : "Hết hàng";
        JLabel stockLabel = new JLabel(stockText);
        info.add(nameLabel);
        info.add(priceLabel);
        info.add(stockLabel);
        card.add(info, BorderLayout.CENTER);

        boolean selectable = product.getStockQuantity() > 0 && product.getFromPrice() != null;
        card.setEnabled(selectable);
        if (selectable) {
            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    openOptionDialog(product);
                }
            });
        } else {
            card.setBackground(new Color(238, 238, 238));
        }
        return card;
    }

    private void openOptionDialog(Product product) {
        try {
            ProductOptionDialog dialog = new ProductOptionDialog(
                    SwingUtilities.getWindowAncestor(this),
                    product,
                    catalogController
            );
            dialog.setVisible(true);
            CartLineRequest request = dialog.getResult();
            if (request != null) {
                int existingQuantity = quantityInCart(product.getProductId());
                if (existingQuantity + request.getQuantity() > product.getStockQuantity()) {
                    throw new ValidationException("Không được bán vượt quá tồn kho.");
                }
                addOrMerge(checkoutController.previewLine(request));
            }
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private int quantityInCart(int productId) {
        int quantity = 0;
        for (CartLine line : cartLines) {
            if (line.getRequest().getProductId() == productId) {
                quantity += line.getRequest().getQuantity();
            }
        }
        return quantity;
    }

    private void addOrMerge(CartLine newLine) throws SQLException {
        String newKey = newLine.getRequest().cartKey();
        for (int i = 0; i < cartLines.size(); i++) {
            CartLine existing = cartLines.get(i);
            if (existing.getRequest().cartKey().equals(newKey)) {
                existing.getRequest().setQuantity(existing.getRequest().getQuantity() + newLine.getRequest().getQuantity());
                cartLines.set(i, checkoutController.previewLine(existing.getRequest()));
                fillCart();
                return;
            }
        }
        cartLines.add(newLine);
        fillCart();
    }

    private void fillCart() {
        cartModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (CartLine line : cartLines) {
            cartModel.addRow(new Object[]{
                    line.getProductName(),
                    optionsText(line),
                    Integer.valueOf(line.getRequest().getQuantity()),
                    MoneyUtils.formatVnd(line.getUnitPrice()),
                    MoneyUtils.formatVnd(line.getLineTotal())
            });
            total = total.add(line.getLineTotal());
        }
        totalLabel.setText(MoneyUtils.formatVnd(total));
    }

    private String optionsText(CartLine line) {
        StringBuilder builder = new StringBuilder(line.getSizeName());
        if (!line.getToppings().isEmpty()) {
            builder.append(" | ");
            for (int i = 0; i < line.getToppings().size(); i++) {
                ToppingSnapshot topping = line.getToppings().get(i);
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(topping.getName());
            }
        }
        String note = line.getRequest().getNote();
        if (note != null && !note.trim().isEmpty()) {
            builder.append(" | ").append(note.trim());
        }
        return builder.toString();
    }

    private void removeSelectedLine() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            UiUtils.showInfo(this, "Chọn dòng cần xóa.");
            return;
        }
        cartLines.remove(cartTable.convertRowIndexToModel(row));
        fillCart();
    }

    private void clearCartWithConfirm() {
        if (cartLines.isEmpty() || UiUtils.confirm(this, "Xóa toàn bộ giỏ hàng?")) {
            cartLines.clear();
            fillCart();
        }
    }

    private void checkout() {
        if (processingCheckout) {
            return;
        }
        if (employeeId == null || employeeId.intValue() <= 0) {
            UiUtils.showError(this, new ValidationException("Tài khoản chưa gắn với nhân viên."));
            return;
        }
        if (cartLines.isEmpty()) {
            UiUtils.showInfo(this, "Giỏ hàng đang trống.");
            return;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartLine line : cartLines) {
            total = total.add(line.getLineTotal());
        }
        PaymentDialog dialog = new PaymentDialog(SwingUtilities.getWindowAncestor(this), total);
        dialog.setVisible(true);
        PaymentInput paymentInput = dialog.getPaymentInput();
        if (paymentInput == null) {
            return;
        }
        processingCheckout = true;
        try {
            CheckoutRequest request = new CheckoutRequest();
            request.setEmployeeId(employeeId.intValue());
            List<CartLineRequest> requests = new ArrayList<CartLineRequest>();
            for (CartLine line : cartLines) {
                requests.add(line.getRequest());
            }
            request.setLines(requests);
            request.setPaymentInput(paymentInput);
            CheckoutResult result = checkoutController.checkout(request);
            UiUtils.showInfo(this, "Đã lưu hóa đơn " + result.getOrderCode() + ". Tiền thừa: " + MoneyUtils.formatVnd(result.getChangeAmount()));
            cartLines.clear();
            fillCart();
            loadProducts();
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        } finally {
            processingCheckout = false;
        }
    }
}
