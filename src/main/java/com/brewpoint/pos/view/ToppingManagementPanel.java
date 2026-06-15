package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.model.Topping;
import com.brewpoint.pos.util.FormLayout;
import com.brewpoint.pos.util.MoneyUtils;
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

public class ToppingManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient CatalogController controller = new CatalogController();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Mã", "Code", "Tên", "Giá", "Trạng thái"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField codeField = new JTextField(10);
    private final JTextField nameField = new JTextField(18);
    private final JTextField priceField = new JTextField(8);
    private final JCheckBox activeBox = new JCheckBox("Đang bán", true);
    private final List<Topping> toppings = new ArrayList<Topping>();
    private int selectedId;

    public ToppingManagementPanel() {
        UiUtils.styleContentPanel(this);
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        add(buildForm(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> selectRow());
        add(new JScrollPane(table), BorderLayout.CENTER);
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
        JButton clearButton = UiUtils.secondaryButton("Làm mới");
        clearButton.addActionListener(e -> clearForm());
        return new FormLayout()
                .addRow("Code", codeField)
                .addRow("Tên", nameField)
                .addRow("Giá", priceField)
                .addFullWidth(activeBox)
                .buildCard(saveButton, deactivateButton, clearButton);
    }

    private void loadData() {
        try {
            toppings.clear();
            toppings.addAll(controller.findToppings(false));
            model.setRowCount(0);
            for (Topping topping : toppings) {
                model.addRow(new Object[]{
                        Integer.valueOf(topping.getToppingId()),
                        topping.getToppingCode(),
                        topping.getName(),
                        MoneyUtils.formatVnd(topping.getExtraPrice()),
                        topping.isActive() ? "Đang bán" : "Ngừng bán"
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
        Topping topping = toppings.get(table.convertRowIndexToModel(row));
        selectedId = topping.getToppingId();
        codeField.setText(topping.getToppingCode());
        nameField.setText(topping.getName());
        priceField.setText(topping.getExtraPrice().toPlainString());
        activeBox.setSelected(topping.isActive());
    }

    private void save() {
        try {
            Topping topping = new Topping();
            topping.setToppingId(selectedId);
            topping.setToppingCode(codeField.getText());
            topping.setName(nameField.getText());
            topping.setExtraPrice(ValidationUtils.requirePositiveMoney(priceField.getText(), "Giá topping"));
            topping.setActive(activeBox.isSelected());
            if (selectedId > 0) {
                controller.updateTopping(topping);
            } else {
                controller.createTopping(topping);
            }
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void deactivate() {
        if (selectedId <= 0) {
            UiUtils.showInfo(this, "Chọn topping cần ngừng bán.");
            return;
        }
        try {
            controller.deactivateTopping(selectedId);
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
