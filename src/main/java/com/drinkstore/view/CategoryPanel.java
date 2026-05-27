package com.drinkstore.view;

import com.drinkstore.controller.CategoryController;
import com.drinkstore.model.Category;
import com.drinkstore.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

public class CategoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient CategoryController controller = new CategoryController();
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Mã loại", "Tên loại", "Mô tả"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField nameField = new JTextField(22);
    private final JTextArea descriptionArea = new JTextArea(4, 22);
    private final JTextField searchField = new JTextField(20);
    private int selectedId = 0;

    public CategoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(buildForm(), BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin loại sản phẩm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Tên loại"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mô tả"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descriptionArea), gbc);

        JButton addButton = new JButton("Thêm");
        addButton.addActionListener(e -> create());
        JButton updateButton = new JButton("Sửa");
        updateButton.addActionListener(e -> update());
        JButton deleteButton = new JButton("Xóa");
        deleteButton.addActionListener(e -> delete());
        JButton clearButton = new JButton("Làm mới");
        clearButton.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel();
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttons, gbc);
        return panel;
    }

    private JPanel buildTable() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Tìm tên loại"));
        searchPanel.add(searchField);
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
                selectRow(table.convertRowIndexToModel(table.getSelectedRow()));
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadData() {
        try {
            fillTable(controller.findAll());
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void search() {
        try {
            fillTable(controller.search(searchField.getText()));
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void create() {
        try {
            controller.create(readForm());
            UiUtil.showInfo(this, "Đã thêm loại sản phẩm.");
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void update() {
        if (selectedId <= 0) {
            UiUtil.showInfo(this, "Chọn loại sản phẩm cần sửa.");
            return;
        }
        try {
            Category category = readForm();
            category.setCategoryId(selectedId);
            controller.update(category);
            UiUtil.showInfo(this, "Đã cập nhật loại sản phẩm.");
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void delete() {
        if (selectedId <= 0) {
            UiUtil.showInfo(this, "Chọn loại sản phẩm cần xóa.");
            return;
        }
        if (!UiUtil.confirm(this, "Xóa loại sản phẩm đã chọn?")) {
            return;
        }
        try {
            controller.delete(selectedId);
            UiUtil.showInfo(this, "Đã xóa loại sản phẩm.");
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private Category readForm() {
        Category category = new Category();
        category.setName(nameField.getText());
        category.setDescription(descriptionArea.getText());
        return category;
    }

    private void selectRow(int row) {
        selectedId = (int) tableModel.getValueAt(row, 0);
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        Object description = tableModel.getValueAt(row, 2);
        descriptionArea.setText(description == null ? "" : String.valueOf(description));
    }

    private void fillTable(List<Category> categories) {
        tableModel.setRowCount(0);
        for (Category category : categories) {
            tableModel.addRow(new Object[]{category.getCategoryId(), category.getName(), category.getDescription()});
        }
    }

    private void clearForm() {
        selectedId = 0;
        nameField.setText("");
        descriptionArea.setText("");
        table.clearSelection();
    }
}
