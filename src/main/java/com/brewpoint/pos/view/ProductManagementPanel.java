package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.model.Category;
import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.model.ProductStatus;
import com.brewpoint.pos.util.ImageService;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UiUtils;
import com.brewpoint.pos.util.ValidationUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient CatalogController controller = new CatalogController();
    private final transient ImageService imageService = new ImageService();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã", "Code", "Tên", "Danh mục", "Giá từ", "Tồn", "Trạng thái"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField codeField = new JTextField(12);
    private final JTextField nameField = new JTextField(18);
    private final JTextField stockField = new JTextField("0", 6);
    private final JTextField imagePathField = new JTextField(22);
    private final JComboBox<Category> categoryCombo = new JComboBox<Category>();
    private final JCheckBox activeBox = new JCheckBox("Đang bán", true);
    private final JLabel previewLabel = new JLabel();
    private final List<Product> products = new ArrayList<Product>();
    private int selectedId;

    public ProductManagementPanel() {
        setLayout(new BorderLayout(8, 8));
        add(buildForm(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> selectRow());
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadCategories();
        loadData();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new BorderLayout());
        JPanel fields = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fields.add(new JLabel("Code"));
        fields.add(codeField);
        fields.add(new JLabel("Tên"));
        fields.add(nameField);
        fields.add(new JLabel("Danh mục"));
        fields.add(categoryCombo);
        fields.add(new JLabel("Tồn"));
        fields.add(stockField);
        fields.add(activeBox);
        form.add(fields, BorderLayout.NORTH);

        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        previewLabel.setIcon(imageService.placeholder(120, 90));
        imagePanel.add(previewLabel);
        imagePanel.add(new JLabel("Ảnh"));
        imagePanel.add(imagePathField);
        JButton chooseButton = new JButton("Chọn ảnh");
        chooseButton.addActionListener(e -> chooseImage());
        JButton removeImageButton = new JButton("Xóa ảnh");
        removeImageButton.addActionListener(e -> {
            imagePathField.setText("");
            previewLabel.setIcon(imageService.placeholder(120, 90));
        });
        imagePanel.add(chooseButton);
        imagePanel.add(removeImageButton);
        form.add(imagePanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton saveButton = UiUtils.primaryButton("Lưu");
        saveButton.addActionListener(e -> save());
        JButton deactivateButton = new JButton("Ngừng bán");
        deactivateButton.addActionListener(e -> deactivate());
        JButton sizeButton = new JButton("Quản lý size");
        sizeButton.addActionListener(e -> openSizeDialog());
        JButton clearButton = new JButton("Làm mới");
        clearButton.addActionListener(e -> clearForm());
        buttons.add(saveButton);
        buttons.add(deactivateButton);
        buttons.add(sizeButton);
        buttons.add(clearButton);
        form.add(buttons, BorderLayout.SOUTH);
        return form;
    }

    private void loadCategories() {
        try {
            categoryCombo.removeAllItems();
            List<Category> categories = controller.findCategories(true);
            for (Category category : categories) {
                categoryCombo.addItem(category);
            }
        } catch (SQLException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void loadData() {
        try {
            products.clear();
            products.addAll(controller.searchProducts("", null, false));
            model.setRowCount(0);
            for (Product product : products) {
                model.addRow(new Object[]{
                        Integer.valueOf(product.getProductId()),
                        product.getProductCode(),
                        product.getName(),
                        product.getCategoryName(),
                        MoneyUtils.formatVnd(product.getFromPrice()),
                        Integer.valueOf(product.getStockQuantity()),
                        product.getStatus().getDisplayName()
                });
            }
        } catch (SQLException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void selectRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Product product = products.get(table.convertRowIndexToModel(row));
        selectedId = product.getProductId();
        codeField.setText(product.getProductCode());
        nameField.setText(product.getName());
        stockField.setText(String.valueOf(product.getStockQuantity()));
        imagePathField.setText(product.getImagePath() == null ? "" : product.getImagePath());
        activeBox.setSelected(product.getStatus().isActive());
        selectCategory(product.getCategoryId());
        previewLabel.setIcon(imageService.loadThumbnail(product.getImagePath(), 120, 90));
    }

    private void selectCategory(int categoryId) {
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            if (categoryCombo.getItemAt(i).getCategoryId() == categoryId) {
                categoryCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                String path = imageService.copyProductImage(file);
                imagePathField.setText(path);
                previewLabel.setIcon(imageService.loadThumbnail(path, 120, 90));
            } catch (RuntimeException ex) {
                UiUtils.showError(this, ex);
            }
        }
    }

    private void save() {
        try {
            Category category = (Category) categoryCombo.getSelectedItem();
            Product product = new Product();
            product.setProductId(selectedId);
            product.setProductCode(codeField.getText());
            product.setName(nameField.getText());
            product.setCategoryId(category == null ? 0 : category.getCategoryId());
            product.setImagePath(imagePathField.getText());
            product.setStockQuantity(ValidationUtils.requireNonNegativeInt(stockField.getText(), "Tồn kho"));
            product.setStatus(activeBox.isSelected() ? ProductStatus.ACTIVE : ProductStatus.INACTIVE);
            if (selectedId > 0) {
                controller.updateProduct(product);
            } else {
                selectedId = controller.createProduct(product);
            }
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void deactivate() {
        if (selectedId <= 0) {
            UiUtils.showInfo(this, "Chọn sản phẩm cần ngừng bán.");
            return;
        }
        try {
            controller.deactivateProduct(selectedId);
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void openSizeDialog() {
        if (selectedId <= 0) {
            UiUtils.showInfo(this, "Chọn sản phẩm trước khi quản lý size.");
            return;
        }
        SizeManagementDialog dialog = new SizeManagementDialog(javax.swing.SwingUtilities.getWindowAncestor(this), selectedId, controller);
        dialog.setVisible(true);
        loadData();
    }

    private void clearForm() {
        selectedId = 0;
        codeField.setText("");
        nameField.setText("");
        stockField.setText("0");
        imagePathField.setText("");
        activeBox.setSelected(true);
        previewLabel.setIcon(imageService.placeholder(120, 90));
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
        table.clearSelection();
    }
}
