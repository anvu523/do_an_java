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

import com.brewpoint.pos.util.UIConstants;

import com.brewpoint.pos.util.UiUtils;

import com.brewpoint.pos.util.ValidationException;



import javax.swing.BorderFactory;

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

    private static final String[] CART_HEADERS = new String[]{"Món", "Tùy chọn", "SL", "Đơn giá", "Thành tiền"};

    private static final int[] CART_MIN_COLUMN_WIDTHS = new int[]{90, 150, 40, 95, 105};



    private final transient CatalogController catalogController = new CatalogController();

    private final transient CheckoutController checkoutController = new CheckoutController();

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

    private final JLabel totalLabel = new JLabel("0 ₫", SwingConstants.RIGHT);

    private final transient List<CartLine> cartLines = new ArrayList<CartLine>();

    private JSplitPane catalogCartSplit;

    private boolean splitDividerInitialized;

    private boolean processingCheckout;



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

        JLabel searchLabel = new JLabel("Tìm");

        UiUtils.styleLabel(searchLabel);

        searchRow.add(searchLabel);

        UiUtils.styleField(searchField);

        searchField.setPreferredSize(new Dimension(220, UIConstants.FORM_FIELD_HEIGHT));

        searchRow.add(searchField);

        JButton searchButton = UiUtils.secondaryButton("Tìm");

        searchButton.addActionListener(e -> loadProducts());

        JButton reloadButton = UiUtils.secondaryButton("Tải lại");

        reloadButton.addActionListener(e -> loadProducts());

        searchRow.add(searchButton);

        searchRow.add(reloadButton);

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

        cartTable.setToolTipText("Double-click để sửa món");

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

        totalLabel.setFont(UIConstants.fontBold(UIConstants.FONT_TOTAL));

        totalLabel.setForeground(UIConstants.PRIMARY);

        bottom.add(totalLabel, BorderLayout.NORTH);



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

        Category selected = (Category) categoryCombo.getSelectedItem();

        Integer categoryId = selected == null || selected.getCategoryId() == 0 ? null : Integer.valueOf(selected.getCategoryId());

        String keyword = searchField.getText();

        productCards.clear();

        productGrid.removeAll();

        productCountLabel.setText("Đang tải...");

        JLabel loadingLabel = new JLabel("Đang tải sản phẩm...");

        loadingLabel.setFont(UIConstants.font(UIConstants.FONT_LABEL));

        productGrid.add(loadingLabel);

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

        } finally {

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

                cartLines.remove(modelRow);

                addOrMerge(checkoutController.previewLine(updated));

            }

        } catch (SQLException | RuntimeException ex) {

            UiUtils.showError(this, ex);

        }

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


