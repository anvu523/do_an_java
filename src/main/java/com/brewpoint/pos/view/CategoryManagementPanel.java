package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.model.Category;
import com.brewpoint.pos.util.FormLayout;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;
import com.brewpoint.pos.util.ValidationUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient CatalogController controller = new CatalogController();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Mã", "Tên", "Thứ tự hiển thị", "Trạng thái"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField nameField = new JTextField(18);
    private final JTextField orderField = new JTextField("0", 6);
    private final JCheckBox activeBox = new JCheckBox("Đang dùng", true);
    private final List<Category> categories = new ArrayList<Category>();
    private int selectedId;

    public CategoryManagementPanel() {
        UiUtils.styleContentPanel(this);
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        add(buildForm(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> selectRow());
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildForm() {
        UiUtils.styleField(nameField);
        UiUtils.styleField(orderField);
        activeBox.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        JButton saveButton = UiUtils.primaryButton("Lưu");
        saveButton.addActionListener(e -> save());
        JButton deleteButton = UiUtils.dangerButton("Ngừng dùng");
        deleteButton.addActionListener(e -> deactivate());
        JButton clearButton = UiUtils.secondaryButton("Nhập mới");
        clearButton.addActionListener(e -> clearForm());
        return new FormLayout()
                .addRow("Tên", nameField)
                .addRow("Thứ tự hiển thị", orderField)
                .addFullWidth(activeBox)
                .buildCard(saveButton, deleteButton, clearButton);
    }

    private void loadData() {
        try {
            categories.clear();
            categories.addAll(controller.findCategories(false));
            model.setRowCount(0);
            for (Category category : categories) {
                model.addRow(new Object[]{
                        Integer.valueOf(category.getCategoryId()),
                        category.getName(),
                        Integer.valueOf(category.getDisplayOrder()),
                        category.isActive() ? "Đang dùng" : "Ngừng dùng"
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
        Category category = categories.get(table.convertRowIndexToModel(row));
        selectedId = category.getCategoryId();
        nameField.setText(category.getName());
        orderField.setText(String.valueOf(category.getDisplayOrder()));
        activeBox.setSelected(category.isActive());
    }

    private void save() {
        try {
            Category category = new Category();
            category.setCategoryId(selectedId);
            category.setName(nameField.getText());
            category.setDisplayOrder(ValidationUtils.requireNonNegativeInt(orderField.getText(), "Thứ tự hiển thị"));
            category.setActive(activeBox.isSelected());
            if (selectedId > 0) {
                controller.updateCategory(category);
            } else {
                controller.createCategory(category);
            }
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void deactivate() {
        if (selectedId <= 0) {
            UiUtils.showInfo(this, "Chọn danh mục cần ngừng dùng.");
            return;
        }
        try {
            controller.deactivateCategory(selectedId);
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void clearForm() {
        selectedId = 0;
        nameField.setText("");
        orderField.setText("0");
        activeBox.setSelected(true);
        table.clearSelection();
    }
}
