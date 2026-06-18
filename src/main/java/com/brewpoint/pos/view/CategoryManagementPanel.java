package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.model.Category;
import com.brewpoint.pos.util.FormLayout;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient CatalogController controller = com.brewpoint.pos.DependencyContainer.getInstance().getCatalogController();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Mã", "Tên", "Thứ tự hiển thị", "Trạng thái"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField nameField = new JTextField(18);
    private final JCheckBox activeBox = new JCheckBox("Đang dùng", true);
    private final List<Category> categories = new ArrayList<Category>();
    private int selectedId;

    public CategoryManagementPanel() {
        UiUtils.styleContentPanel(this);
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        add(buildForm(), BorderLayout.NORTH);
        
        UiUtils.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> selectRow());
        
        // Setup Drag and Drop
        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(new TableRowTransferHandler());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadData();
            }
        });
    }

    private JPanel buildForm() {
        UiUtils.styleField(nameField);
        activeBox.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        
        JButton saveButton = UiUtils.primaryButton("Lưu");
        saveButton.addActionListener(e -> save());
        JButton deleteButton = UiUtils.dangerButton("Ngừng dùng");
        deleteButton.addActionListener(e -> deactivate());
        JButton clearButton = UiUtils.secondaryButton("Nhập mới");
        clearButton.addActionListener(e -> clearForm());
        
        return new FormLayout()
                .addRow("Tên", nameField)
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
                        category.getCategoryId(),
                        category.getName(),
                        category.getDisplayOrder(),
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
        activeBox.setSelected(category.isActive());
    }

    private void save() {
        try {
            Category category = new Category();
            category.setCategoryId(selectedId);
            category.setName(nameField.getText());
            // If new category, add it to the bottom
            int order = categories.size() + 1;
            if (selectedId > 0) {
                // Keep the current display order when updating an existing category
                Category existing = categories.stream().filter(c -> c.getCategoryId() == selectedId).findFirst().orElse(null);
                if (existing != null) {
                    order = existing.getDisplayOrder();
                }
            }
            category.setDisplayOrder(order);
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
        activeBox.setSelected(true);
        table.clearSelection();
    }

    private class TableRowTransferHandler extends TransferHandler {
        private final DataFlavor localObjectFlavor;
        private int sourceRow = -1;

        public TableRowTransferHandler() {
            localObjectFlavor = new DataFlavor(Integer.class, "Integer Row Index");
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTable table = (JTable) c;
            sourceRow = table.getSelectedRow();
            return new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{localObjectFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return flavor.equals(localObjectFlavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                    if (isDataFlavorSupported(flavor)) {
                        return sourceRow;
                    }
                    throw new UnsupportedFlavorException(flavor);
                }
            };
        }

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }

        @Override
        public boolean canImport(TransferSupport info) {
            boolean isSupported = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
            table.setCursor(isSupported ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
            return isSupported;
        }

        @Override
        public boolean importData(TransferSupport info) {
            JTable target = (JTable) info.getComponent();
            JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
            int index = dl.getRow();
            int max = table.getModel().getRowCount();
            if (index < 0 || index > max) {
                index = max;
            }
            target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            
            try {
                int rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
                if (rowFrom != -1 && rowFrom != index) {
                    if (index > rowFrom) {
                        index--; 
                    }
                    
                    Category c = categories.remove(rowFrom);
                    categories.add(index, c);

                    for (int i = 0; i < categories.size(); i++) {
                        categories.get(i).setDisplayOrder(i + 1);
                    }

                    controller.updateCategoryDisplayOrders(categories);
                    
                    loadData();
                    table.getSelectionModel().setSelectionInterval(index, index);
                    return true;
                }
            } catch (Exception e) {
                UiUtils.showError(CategoryManagementPanel.this, e);
            }
            return false;
        }

        @Override
        protected void exportDone(JComponent c, Transferable t, int act) {
            if (act == TransferHandler.MOVE) {
                table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }
}
