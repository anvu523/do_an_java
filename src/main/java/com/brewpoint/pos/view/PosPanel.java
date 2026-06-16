package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;

import com.brewpoint.pos.controller.CheckoutController;

import com.brewpoint.pos.controller.ReportController;

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

import com.brewpoint.pos.util.UIConstants;

import com.brewpoint.pos.util.UiUtils;

import com.brewpoint.pos.util.ValidationException;

import javax.swing.BorderFactory;

import javax.swing.Box;

import javax.swing.BoxLayout;

import javax.swing.JButton;

import javax.swing.JComboBox;

import javax.swing.JLabel;

import javax.swing.JPanel;

import javax.swing.JScrollPane;

import javax.swing.JSplitPane;

import javax.swing.JTable;

import javax.swing.JTextField;

import javax.swing.ScrollPaneConstants;

import javax.swing.SwingConstants;

import javax.swing.SwingUtilities;

import javax.swing.SwingWorker;

import javax.swing.event.ChangeEvent;

import javax.swing.event.ChangeListener;

import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;

import java.awt.Dimension;

import java.awt.FlowLayout;

import java.awt.GridLayout;

import java.awt.event.ComponentAdapter;

import java.awt.event.ComponentEvent;

import java.awt.event.MouseAdapter;

import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;

import java.math.BigDecimal;

import java.sql.SQLException;

import java.util.ArrayList;

import java.util.List;

public class PosPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String[] CART_HEADERS = new String[]{"Món", "Cỡ & topping", "SL", "Đơn giá", "Thành tiền"};

    private static final int[] CART_MIN_COLUMN_WIDTHS = new int[]{90, 150, 40, 95, 105};

    private final transient CatalogController catalogController = com.brewpoint.pos.DependencyContainer.getInstance().getCatalogController();

    private final transient CheckoutController checkoutController = com.brewpoint.pos.DependencyContainer.getInstance().getCheckoutController();

    private final transient ReportController reportController = com.brewpoint.pos.DependencyContainer.getInstance().getReportController();

    private final transient ImageService imageService = new ImageService();

    private final Integer employeeId;

    private final JComboBox<Category> categoryCombo = new JComboBox<Category>();

    private final JTextField searchField = new JTextField(22);

    private final JLabel productCountLabel = new JLabel("0 sản phẩm");

    private final ProductGridPanel productGrid = new ProductGridPanel();

    private final JScrollPane productScrollPane = new JScrollPane(productGrid);

    private final List<ProductCardPanel> productCards = new ArrayList<ProductCardPanel>();

    private final DefaultTableModel cartModel = new DefaultTableModel(CART_HEADERS, 0) {

        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {

            return false;

        }

    };

    private final JTable cartTable = new JTable(cartModel);

    private final JLabel subtotalLabel = new JLabel("0 ₫", SwingConstants.RIGHT);

    private final JLabel discountLabel = new JLabel("0 ₫", SwingConstants.RIGHT);

    private final JPanel discountRow = new JPanel(new BorderLayout());

    private final JLabel paymentTotalLabel = new JLabel("0 ₫", SwingConstants.RIGHT);

    private final transient List<CartLine> cartLines = new ArrayList<CartLine>();

    private JSplitPane catalogCartSplit;

    private boolean splitDividerInitialized;

    private boolean processingCheckout;

    private boolean optionDialogOpen;

    public PosPanel(Integer employeeId) {

        this.employeeId = employeeId;

        UiUtils.styleContentPanel(this);

        setLayout(new BorderLayout());

        catalogCartSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildCatalogPanel(), buildCartPanel());

        catalogCartSplit.setResizeWeight(1.0d - UIConstants.CART_WIDTH_RATIO);

        catalogCartSplit.setBorder(null);

        catalogCartSplit.setDividerSize(10);

        catalogCartSplit.setContinuousLayout(true);

        catalogCartSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                relayoutProductGrid();

            }

        });

        add(catalogCartSplit, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {

                initializeSplitDivider();

                relayoutProductGrid();

            }

            public void componentShown(ComponentEvent e) {

                Category selected = (Category) categoryCombo.getSelectedItem();
                int oldId = selected != null ? selected.getCategoryId() : -1;
                
                loadCategories();
                
                for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                    Category cat = categoryCombo.getItemAt(i);
                    if (cat != null && cat.getCategoryId() == oldId) {
                        categoryCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                loadProducts(false);
            }
        });

        productScrollPane.getViewport().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {

                relayoutProductGrid();

            }

        });

        loadCategories();

        loadProducts();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                initializeSplitDivider();

                relayoutProductGrid();

            }

        });

    }

    private JPanel buildCatalogPanel() {

        JPanel panel = new JPanel(new BorderLayout(UIConstants.SPACING_SM, UIConstants.SPACING_SM));

        panel.setBackground(UIConstants.BG_APP);

        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIConstants.SPACING_SM));

        panel.setMinimumSize(new Dimension(360, 0));

        JPanel top = new JPanel();

        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        top.setBackground(UIConstants.BG_APP);

        top.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_SM, 0, UIConstants.SPACING_SM, 0));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));

        filterRow.setOpaque(false);

        JLabel categoryLabel = new JLabel("Danh mục");

        UiUtils.styleLabel(categoryLabel);

        filterRow.add(categoryLabel);

        categoryCombo.setPreferredSize(new Dimension(180, 36));

        filterRow.add(categoryCombo);

        productCountLabel.setFont(UIConstants.font(UIConstants.FONT_LABEL));

        productCountLabel.setForeground(UIConstants.TEXT_MUTED);

        filterRow.add(productCountLabel);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, UIConstants.SPACING_SM));

        searchRow.setOpaque(false);

        JLabel searchLabel = new JLabel("Tìm món");

        UiUtils.styleLabel(searchLabel);

        searchRow.add(searchLabel);

        UiUtils.styleField(searchField);
        UiUtils.installPlaceholder(searchField, "VD: trà sữa");

        searchField.setPreferredSize(new Dimension(220, UIConstants.FORM_FIELD_HEIGHT));

        searchRow.add(searchField);

        JButton searchButton = UiUtils.secondaryButton("Tìm");

        searchButton.addActionListener(e -> loadProducts());

        searchRow.add(searchButton);

        top.add(filterRow);

        top.add(searchRow);

        panel.add(top, BorderLayout.NORTH);

        productGrid.setBackground(UIConstants.BG_APP);

        productScrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));

        productScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        productScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        productScrollPane.getVerticalScrollBar().setUnitIncrement(UIConstants.CARD_HEIGHT / 2);

        panel.add(productScrollPane, BorderLayout.CENTER);

        return panel;

    }

    private JPanel buildCartPanel() {

        JPanel panel = new JPanel(new BorderLayout(UIConstants.SPACING_SM, UIConstants.SPACING_SM));

        panel.setBackground(UIConstants.BG_PANEL);

        int requiredWidth = measureRequiredCartWidth();

        panel.setMinimumSize(new Dimension(requiredWidth, 0));

        panel.setPreferredSize(new Dimension(requiredWidth, 0));

        UiUtils.panelBorder(panel, "Giỏ hàng");

        UiUtils.configureTable(cartTable);

        cartTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        cartTable.setToolTipText("Đúp chuột để sửa món");

        cartTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {

                    int row = cartTable.rowAtPoint(e.getPoint());

                    if (row >= 0) {

                        cartTable.setRowSelectionInterval(row, row);

                        editSelectedLine();

                    }

                }

            }

        });

        JScrollPane cartScroll = new JScrollPane(cartTable);

        cartScroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(cartScroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(UIConstants.SPACING_SM, UIConstants.SPACING_SM));

        bottom.setOpaque(false);

        bottom.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_SM, UIConstants.SPACING_SM,

                UIConstants.SPACING_SM, UIConstants.SPACING_SM));

        bottom.add(buildCartTotalsPanel(), BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 3, UIConstants.SPACING_SM, 0));

        buttons.setOpaque(false);

        JButton removeButton = UiUtils.secondaryButton("Xóa dòng");

        removeButton.addActionListener(e -> removeSelectedLine());

        JButton clearButton = UiUtils.dangerButton("Xóa giỏ");

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

    private JPanel buildCartTotalsPanel() {

        JPanel totals = new JPanel();

        totals.setLayout(new BoxLayout(totals, BoxLayout.Y_AXIS));

        totals.setOpaque(false);

        totals.add(buildTotalRow("Tổng tiền hàng", subtotalLabel, false));

        discountRow.setLayout(new BorderLayout());

        discountRow.setOpaque(false);

        discountRow.add(buildTotalRow("Giảm giá", discountLabel, false), BorderLayout.CENTER);

        discountRow.setVisible(false);

        totals.add(discountRow);

        totals.add(Box.createVerticalStrut(UIConstants.SPACING_SM));

        totals.add(buildTotalRow("Tổng thanh toán", paymentTotalLabel, true));

        return totals;

    }

    private JPanel buildTotalRow(String caption, JLabel valueLabel, boolean emphasize) {

        JPanel row = new JPanel(new BorderLayout(UIConstants.SPACING_SM, 0));

        row.setOpaque(false);

        JLabel captionLabel = new JLabel(caption);

        captionLabel.setFont(emphasize ? UIConstants.fontBold(UIConstants.FONT_LABEL)

                : UIConstants.font(UIConstants.FONT_LABEL));

        captionLabel.setForeground(emphasize ? UIConstants.TEXT_PRIMARY : UIConstants.TEXT_MUTED);

        if (emphasize) {

            valueLabel.setFont(UIConstants.fontBold(UIConstants.FONT_TOTAL));

            valueLabel.setForeground(UIConstants.PRIMARY);

        } else {

            valueLabel.setFont(UIConstants.font(UIConstants.FONT_LABEL));

            valueLabel.setForeground(UIConstants.TEXT_PRIMARY);

        }

        row.add(captionLabel, BorderLayout.WEST);

        row.add(valueLabel, BorderLayout.EAST);

        return row;

    }

    private int measureRequiredCartWidth() {

        int tableWidth = UiUtils.requiredTableWidth(cartTable, CART_HEADERS, CART_MIN_COLUMN_WIDTHS, 24);

        return tableWidth + 36;

    }

    private void initializeSplitDivider() {

        if (splitDividerInitialized) {

            return;

        }

        int width = catalogCartSplit.getWidth();

        if (width <= 0) {

            return;

        }

        int dividerLocation = (int) Math.round(width * (1.0d - UIConstants.CART_WIDTH_RATIO));

        catalogCartSplit.setDividerLocation(dividerLocation);

        splitDividerInitialized = true;

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

        loadProducts(true);

    }

    private void loadProducts(boolean showLoading) {

        Category selected = (Category) categoryCombo.getSelectedItem();

        Integer categoryId = selected == null || selected.getCategoryId() == 0 ? null : Integer.valueOf(selected.getCategoryId());

        String keyword = UiUtils.readFieldText(searchField);

        if (showLoading) {

            productCards.clear();

            productGrid.removeAll();

            productCountLabel.setText("Đang tải...");

            JLabel loadingLabel = new JLabel("Đang tải sản phẩm...");

            loadingLabel.setFont(UIConstants.font(UIConstants.FONT_LABEL));

            productGrid.add(loadingLabel);

            productGrid.revalidate();

            productGrid.repaint();

        }

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

        productCards.clear();

        productGrid.removeAll();

        if (products == null || products.isEmpty()) {

            productCountLabel.setText("0 sản phẩm");

            JLabel emptyLabel = new JLabel("Không tìm thấy sản phẩm phù hợp.");

            emptyLabel.setFont(UIConstants.font(UIConstants.FONT_LABEL));

            productGrid.add(emptyLabel);

        } else {

            productCountLabel.setText(products.size() + " sản phẩm");

            for (int i = 0; i < products.size(); i++) {

                final Product product = products.get(i);

                final ProductCardPanel[] cardRef = new ProductCardPanel[1];

                cardRef[0] = new ProductCardPanel(product, imageService, new Runnable() {

                    public void run() {

                        clearCardSelection();

                        cardRef[0].setSelected(true);

                        openOptionDialog(product);

                    }

                });

                productCards.add(cardRef[0]);

                productGrid.add(cardRef[0]);

            }

            relayoutProductGrid();

        }

        productGrid.revalidate();

        productGrid.repaint();

    }

    private void clearCardSelection() {

        for (int i = 0; i < productCards.size(); i++) {

            productCards.get(i).clearSelected();

        }

    }

    private void relayoutProductGrid() {

        if (productCards.isEmpty()) {

            return;

        }

        int viewportWidth = productScrollPane.getViewport().getWidth();

        if (viewportWidth <= 0) {

            return;

        }

        int[] layout = UIConstants.cardLayoutForViewport(viewportWidth);

        int cardWidth = layout[1];

        int imageWidth = Math.max(120, cardWidth - UIConstants.CARD_PADDING * 2);

        int imageHeight = Math.max(96, (int) Math.round(imageWidth * 0.75d));

        int cardHeight = imageHeight + 112;

        for (int i = 0; i < productCards.size(); i++) {

            productCards.get(i).setCardSize(cardWidth, cardHeight, imageWidth, imageHeight);

        }

        productGrid.revalidate();

        productGrid.repaint();

        productScrollPane.revalidate();

        productScrollPane.repaint();

    }

    private void openOptionDialog(Product product) {

        if (optionDialogOpen) {
            return;
        }
        optionDialogOpen = true;

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

                addToCart(checkoutController.previewLine(request));

            }

        } catch (SQLException | RuntimeException ex) {

            UiUtils.showError(this, ex);

        } finally {

            optionDialogOpen = false;
            clearCardSelection();

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

    private int quantityInCartExcept(int productId, int excludeIndex) {

        int quantity = 0;

        for (int i = 0; i < cartLines.size(); i++) {

            if (i == excludeIndex) {

                continue;

            }

            CartLine line = cartLines.get(i);

            if (line.getRequest().getProductId() == productId) {

                quantity += line.getRequest().getQuantity();

            }

        }

        return quantity;

    }

    private void addToCart(CartLine newLine) {

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

        BigDecimal discount = BigDecimal.ZERO;

        BigDecimal paymentTotal = total.subtract(discount);

        subtotalLabel.setText(MoneyUtils.formatVnd(total));

        discountLabel.setText(MoneyUtils.formatVnd(discount));

        paymentTotalLabel.setText(MoneyUtils.formatVnd(paymentTotal));

        discountRow.setVisible(discount.signum() > 0);

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

    private void editSelectedLine() {

        int row = cartTable.getSelectedRow();

        if (row < 0) {

            return;

        }

        int modelRow = cartTable.convertRowIndexToModel(row);

        CartLine line = cartLines.get(modelRow);

        try {

            Product product = catalogController.findProduct(line.getRequest().getProductId());

            if (product == null) {

                throw new ValidationException("Không tìm thấy sản phẩm.");

            }

            ProductOptionDialog dialog = new ProductOptionDialog(

                    SwingUtilities.getWindowAncestor(this),

                    product,

                    catalogController,

                    line.getRequest(),

                    true

            );

            dialog.setVisible(true);

            CartLineRequest updated = dialog.getResult();

            if (updated != null) {

                int otherQty = quantityInCartExcept(line.getRequest().getProductId(), modelRow);

                if (otherQty + updated.getQuantity() > product.getStockQuantity()) {

                    throw new ValidationException("Không được bán vượt quá tồn kho.");

                }

                cartLines.set(modelRow, checkoutController.previewLine(updated));

                fillCart();

            }

        } catch (SQLException | RuntimeException ex) {

            UiUtils.showError(this, ex);

        }

    }

    private void clearCartWithConfirm() {

        if (cartLines.isEmpty() || UiUtils.confirm(this, "Bạn muốn xóa hết món trong giỏ?")) {

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

            CheckoutSuccessDialog successDialog = new CheckoutSuccessDialog(

                    SwingUtilities.getWindowAncestor(this),

                    result,

                    paymentInput,

                    reportController,

                    new Runnable() {

                        public void run() {

                            startNewOrder();

                        }

                    });

            successDialog.setVisible(true);

        } catch (SQLException | RuntimeException ex) {

            UiUtils.showError(this, ex);

        } finally {

            processingCheckout = false;

        }

    }

    private void startNewOrder() {
        for (CartLine line : cartLines) {
            int productId = line.getRequest().getProductId();
            int qty = line.getRequest().getQuantity();
            for (ProductCardPanel card : productCards) {
                if (card.getProduct().getProductId() == productId) {
                    card.reduceStock(qty);
                    break;
                }
            }
        }
        cartLines.clear();
        fillCart();
        searchField.requestFocusInWindow();
    }

}
