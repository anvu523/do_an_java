package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.model.ProductSize;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UiUtils;
import com.brewpoint.pos.util.ValidationUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SizeManagementDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final int productId;
    private final transient CatalogController controller;
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Mã", "Code", "Tên", "Giá", "Trạng thái"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField codeField = new JTextField(8);
    private final JTextField nameField = new JTextField(12);
    private final JTextField priceField = new JTextField(8);
    private final JCheckBox activeBox = new JCheckBox("Đang bán", true);
    private final List<ProductSize> sizes = new ArrayList<ProductSize>();
    private int selectedId;

    public SizeManagementDialog(Window owner, int productId, CatalogController controller) {
        super(owner, "Quản lý size", Dialog.ModalityType.APPLICATION_MODAL);
        this.productId = productId;
        this.controller = controller;
        setSize(620, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));
        add(buildForm(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> selectRow());
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Code"));
        panel.add(codeField);
        panel.add(new JLabel("Tên"));
        panel.add(nameField);
        panel.add(new JLabel("Giá"));
        panel.add(priceField);
        panel.add(activeBox);
        JButton saveButton = UiUtils.primaryButton("Lưu");
        saveButton.addActionListener(e -> save());
        JButton deactivateButton = new JButton("Ngừng bán");
        deactivateButton.addActionListener(e -> deactivate());
        JButton clearButton = new JButton("Làm mới");
        clearButton.addActionListener(e -> clearForm());
        panel.add(saveButton);
        panel.add(deactivateButton);
        panel.add(clearButton);
        return panel;
    }

    private void loadData() {
        try {
            sizes.clear();
            sizes.addAll(controller.findSizes(productId, false));
            model.setRowCount(0);
            for (ProductSize size : sizes) {
                model.addRow(new Object[]{
                        Integer.valueOf(size.getProductSizeId()),
                        size.getSizeCode(),
                        size.getSizeName(),
                        MoneyUtils.formatVnd(size.getSalePrice()),
                        size.isActive() ? "Đang bán" : "Ngừng bán"
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
        ProductSize size = sizes.get(table.convertRowIndexToModel(row));
        selectedId = size.getProductSizeId();
        codeField.setText(size.getSizeCode());
        nameField.setText(size.getSizeName());
        priceField.setText(size.getSalePrice().toPlainString());
        activeBox.setSelected(size.isActive());
    }

    private void save() {
        try {
            ProductSize size = new ProductSize();
            size.setProductSizeId(selectedId);
            size.setProductId(productId);
            size.setSizeCode(codeField.getText());
            size.setSizeName(nameField.getText());
            size.setSalePrice(ValidationUtils.requirePositiveMoney(priceField.getText(), "Giá size"));
            size.setActive(activeBox.isSelected());
            if (selectedId > 0) {
                controller.updateSize(size);
            } else {
                controller.createSize(size);
            }
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void deactivate() {
        if (selectedId <= 0) {
            UiUtils.showInfo(this, "Chọn size cần ngừng bán.");
            return;
        }
        try {
            controller.deactivateSize(selectedId);
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void clearForm() {
        selectedId = 0;
        codeField.setText("");
        nameField.setText("");
        priceField.setText("");
        activeBox.setSelected(true);
        table.clearSelection();
    }
}
