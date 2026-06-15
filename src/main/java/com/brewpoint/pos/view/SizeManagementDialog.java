package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.model.ProductSize;
import com.brewpoint.pos.util.FormLayout;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;
import com.brewpoint.pos.util.ValidationUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Window;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SizeManagementDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final int productId;
    private final transient CatalogController controller;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã cỡ", "Tên cỡ", "Giá bán", "Trạng thái"}, 0) {
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
        super(owner, "Quản lý cỡ & giá", Dialog.ModalityType.APPLICATION_MODAL);
        this.productId = productId;
        this.controller = controller;
        setSize(680, 480);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(UIConstants.BG_APP);
        JPanel root = new JPanel(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        root.setBackground(UIConstants.BG_APP);
        root.setBorder(UiUtils.emptyBorder(UIConstants.SPACING_MD));
        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);
        root.add(buildForm(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> selectRow());
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildForm() {
        UiUtils.styleField(codeField);
        UiUtils.styleField(nameField);
        UiUtils.styleField(priceField);
        activeBox.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        JButton saveButton = UiUtils.primaryButton("Lưu");
        saveButton.addActionListener(e -> save());
        JButton deactivateButton = UiUtils.dangerButton("Ngừng bán");
        deactivateButton.addActionListener(e -> deactivate());
        JButton clearButton = UiUtils.secondaryButton("Nhập mới");
        clearButton.addActionListener(e -> clearForm());
        return new FormLayout()
                .addRow("Mã cỡ", codeField)
                .addRow("Tên cỡ", nameField)
                .addRow("Giá bán", priceField)
                .addFullWidth(activeBox)
                .buildCard(saveButton, deactivateButton, clearButton);
    }

    private void loadData() {
        try {
            sizes.clear();
            sizes.addAll(controller.findSizes(productId, false));
            model.setRowCount(0);
            for (ProductSize size : sizes) {
                model.addRow(new Object[]{
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
            size.setSalePrice(ValidationUtils.requirePositiveMoney(priceField.getText(), "Giá cỡ ly"));
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
            UiUtils.showInfo(this, "Chọn cỡ ly cần ngừng bán.");
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
