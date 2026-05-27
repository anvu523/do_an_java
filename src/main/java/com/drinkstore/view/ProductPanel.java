package com.drinkstore.view;

import com.drinkstore.controller.CategoryController;
import com.drinkstore.controller.ProductController;
import com.drinkstore.model.Category;
import com.drinkstore.model.Product;
import com.drinkstore.model.ProductStatus;
import com.drinkstore.util.UiUtil;
import com.drinkstore.util.ValidationUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient ProductController productController = new ProductController();
    private final transient CategoryController categoryController = new CategoryController();
    private final boolean readOnly;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Mã SP", "Tên sản phẩm", "Loại", "Giá bán", "Tồn kho", "Trạng thái"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField nameField = new JTextField(22);
    private final JTextField priceField = new JTextField(22);
    private final JTextField stockField = new JTextField(22);
    private final JComboBox<Category> categoryCombo = new JComboBox<>();
    private final JComboBox<ProductStatus> statusCombo = new JComboBox<>(ProductStatus.values());
    private final JTextField searchField = new JTextField(18);
    private final JComboBox<Category> filterCategoryCombo = new JComboBox<>();
    private final transient List<Product> currentProducts = new ArrayList<>();
    private int selectedId = 0;

    public ProductPanel(boolean readOnly) {
        this.readOnly = readOnly;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(buildForm(), BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);
        loadCategories();
        loadData();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(panel, gbc, 0, "Tên sản phẩm", nameField);
        addFormRow(panel, gbc, 1, "Loại", categoryCombo);
        addFormRow(panel, gbc, 2, "Giá bán", priceField);
        addFormRow(panel, gbc, 3, "Tồn kho", stockField);
        addFormRow(panel, gbc, 4, "Trạng thái", statusCombo);

        JButton addButton = new JButton("Thêm");
        addButton.addActionListener(e -> create());
        JButton updateButton = new JButton("Sửa");
        updateButton.addActionListener(e -> update());
        JButton deleteButton = new JButton("Xóa");
        deleteButton.addActionListener(e -> delete());
        JButton clearButton = new JButton("Làm mới");
        clearButton.addActionListener(e -> clearForm());

        addButton.setEnabled(!readOnly);
        updateButton.setEnabled(!readOnly);
        deleteButton.setEnabled(!readOnly);

        JPanel buttons = new JPanel();
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttons, gbc);
        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JPanel buildTable() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Tên"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Loại"));
        searchPanel.add(filterCategoryCombo);
        JButton searchButton = new JButton("Tìm");
        searchButton.addActionListener(e -> search());
        JButton reloadButton = new JButton("Tải lại");
        reloadButton.addActionListener(e -> loadData());
        searchPanel.add(searchButton);
        searchPanel.add(reloadButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        UiUtil.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                selectProduct(table.convertRowIndexToModel(table.getSelectedRow()));
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryController.findAll();
            categoryCombo.removeAllItems();
            filterCategoryCombo.removeAllItems();
            filterCategoryCombo.addItem(new Category(0, "Tất cả", ""));
            for (Category category : categories) {
                categoryCombo.addItem(category);
                filterCategoryCombo.addItem(category);
            }
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void loadData() {
        try {
            fillTable(readOnly ? productController.search("", null, true) : productController.findAll());
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void search() {
        try {
            Category selectedCategory = (Category) filterCategoryCombo.getSelectedItem();
            Integer categoryId = selectedCategory == null || selectedCategory.getCategoryId() == 0
                    ? null
                    : selectedCategory.getCategoryId();
            fillTable(productController.search(searchField.getText(), categoryId, readOnly));
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void create() {
        try {
            productController.create(readForm());
            UiUtil.showInfo(this, "Đã thêm sản phẩm.");
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void update() {
        if (selectedId <= 0) {
            UiUtil.showInfo(this, "Chọn sản phẩm cần sửa.");
            return;
        }
        try {
            Product product = readForm();
            product.setProductId(selectedId);
            productController.update(product);
            UiUtil.showInfo(this, "Đã cập nhật sản phẩm.");
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void delete() {
        if (selectedId <= 0) {
            UiUtil.showInfo(this, "Chọn sản phẩm cần xóa.");
            return;
        }
        if (!UiUtil.confirm(this, "Xóa sản phẩm đã chọn?")) {
            return;
        }
        try {
            productController.delete(selectedId);
            UiUtil.showInfo(this, "Đã xóa sản phẩm.");
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private Product readForm() {
        Category category = (Category) categoryCombo.getSelectedItem();
        Product product = new Product();
        product.setName(nameField.getText());
        product.setCategoryId(category == null ? 0 : category.getCategoryId());
        product.setCategoryName(category == null ? "" : category.getName());
        product.setPrice(ValidationUtil.requirePositiveMoney(priceField.getText(), "Giá bán"));
        product.setStockQuantity(ValidationUtil.requireNonNegativeInt(stockField.getText(), "Số lượng tồn"));
        product.setStatus((ProductStatus) statusCombo.getSelectedItem());
        return product;
    }

    private void selectProduct(int row) {
        Product product = currentProducts.get(row);
        selectedId = product.getProductId();
        nameField.setText(product.getName());
        priceField.setText(product.getPrice().toPlainString());
        stockField.setText(String.valueOf(product.getStockQuantity()));
        statusCombo.setSelectedItem(product.getStatus());
        selectCategory(categoryCombo, product.getCategoryId());
    }

    private void selectCategory(JComboBox<Category> comboBox, int categoryId) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).getCategoryId() == categoryId) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void fillTable(List<Product> products) {
        currentProducts.clear();
        currentProducts.addAll(products);
        tableModel.setRowCount(0);
        for (Product product : products) {
            BigDecimal price = product.getPrice();
            tableModel.addRow(new Object[]{
                    product.getProductId(),
                    product.getName(),
                    product.getCategoryName(),
                    UiUtil.money(price),
                    product.getStockQuantity(),
                    product.getStatus().getDisplayName()
            });
        }
    }

    private void clearForm() {
        selectedId = 0;
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
        statusCombo.setSelectedItem(ProductStatus.ACTIVE);
        table.clearSelection();
    }
}
