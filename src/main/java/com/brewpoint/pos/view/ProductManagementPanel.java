package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.model.Category;
import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.model.ProductStatus;
import com.brewpoint.pos.util.FormLayout;
import com.brewpoint.pos.util.ImageService;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UIConstants;
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
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient CatalogController controller = new CatalogController();
    private final transient ImageService imageService = new ImageService();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã món", "Tên", "Danh mục", "Giá thấp nhất", "Tồn kho", "Trạng thái"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField codeField = new JTextField(12);
    private final JTextField nameField = new JTextField(18);
    private final JTextField stockField = new JTextField("0", 6);
    private String imagePath = "";
    private final JComboBox<Category> categoryCombo = new JComboBox<Category>();
    private final JCheckBox activeBox = new JCheckBox("Đang bán", true);
    private final JLabel previewLabel = new JLabel();
    private final List<Product> products = new ArrayList<Product>();
    private int selectedId;

    public ProductManagementPanel() {
        UiUtils.styleContentPanel(this);
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        add(buildForm(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> selectRow());
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadCategories();
        loadData();
    }

    private JPanel buildForm() {
        UiUtils.styleField(codeField);
        UiUtils.styleField(nameField);
        UiUtils.styleField(stockField);
        categoryCombo.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        categoryCombo.setPreferredSize(new java.awt.Dimension(220, UIConstants.FORM_FIELD_HEIGHT));
        activeBox.setFont(UIConstants.font(UIConstants.FONT_LABEL));

        JPanel imageRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        imageRow.setOpaque(false);
        previewLabel.setIcon(imageService.placeholder(160, 120));
        imageRow.add(previewLabel);
        JButton chooseButton = UiUtils.secondaryButton("Chọn ảnh");
        chooseButton.addActionListener(e -> chooseImage());
        JButton removeImageButton = UiUtils.dangerButton("Xóa ảnh");
        removeImageButton.addActionListener(e -> {
            imagePath = "";
            previewLabel.setIcon(imageService.placeholder(160, 120));
        });
        imageRow.add(chooseButton);
        imageRow.add(removeImageButton);

        JButton saveButton = UiUtils.primaryButton("Lưu");
        saveButton.addActionListener(e -> save());
        JButton deactivateButton = UiUtils.dangerButton("Ngừng bán");
        deactivateButton.addActionListener(e -> deactivate());
        JButton sizeButton = UiUtils.secondaryButton("Quản lý cỡ & giá");
        sizeButton.addActionListener(e -> openSizeDialog());
        JButton clearButton = UiUtils.secondaryButton("Nhập mới");
        clearButton.addActionListener(e -> clearForm());

        JPanel fields = new FormLayout()
                .addRow("Mã sản phẩm", codeField)
                .addRow("Tên sản phẩm", nameField)
                .addRow("Danh mục", categoryCombo)
                .addRow("Tồn kho", stockField)
                .addFullWidth(activeBox)
                .addFullWidth(imageRow)
                .build();

        return UiUtils.wrapFormCard(fields, saveButton, deactivateButton, sizeButton, clearButton);
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
        imagePath = product.getImagePath() == null ? "" : product.getImagePath();
        activeBox.setSelected(product.getStatus().isActive());
        selectCategory(product.getCategoryId());
        previewLabel.setIcon(imageService.loadThumbnail(imagePath, 160, 120));
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
                imagePath = path;
                previewLabel.setIcon(imageService.loadThumbnail(path, 160, 120));
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
            product.setImagePath(imagePath);
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
            UiUtils.showInfo(this, "Chọn sản phẩm trước khi quản lý cỡ và giá.");
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
        imagePath = "";
        activeBox.setSelected(true);
        previewLabel.setIcon(imageService.placeholder(160, 120));
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
        table.clearSelection();
    }
}
