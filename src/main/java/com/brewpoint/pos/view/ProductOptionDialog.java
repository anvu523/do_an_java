package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.pricing.BaseDrink;
import com.brewpoint.pos.pricing.DrinkComponent;
import com.brewpoint.pos.pricing.ToppingDecorator;
import com.brewpoint.pos.model.CartLineRequest;
import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.model.ProductSize;
import com.brewpoint.pos.model.Topping;
import com.brewpoint.pos.util.ImageService;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductOptionDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final int DIALOG_WIDTH = 780;
    private static final int DIALOG_HEIGHT = 760;

    private final Product product;
    private final transient ImageService imageService = new ImageService();
    private final boolean editMode;
    private final List<ProductSize> sizes = new ArrayList<ProductSize>();
    private final List<Topping> toppings = new ArrayList<Topping>();
    private final Map<JRadioButton, ProductSize> sizeButtons = new LinkedHashMap<JRadioButton, ProductSize>();
    private final Map<JCheckBox, Topping> toppingBoxes = new LinkedHashMap<JCheckBox, Topping>();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JTextField noteField = new JTextField();
    private final JLabel unitPriceLabel = new JLabel("0 ₫");
    private final JLabel lineTotalLabel = new JLabel("0 ₫");
    private CartLineRequest result;

    public ProductOptionDialog(Window owner, Product product, CatalogController catalogController) throws SQLException {
        this(owner, product, catalogController, null, false);
    }

    public ProductOptionDialog(Window owner, Product product, CatalogController catalogController,
                               CartLineRequest initial, boolean editMode) throws SQLException {
        super(owner, editMode ? "Sửa món" : "Chọn món", Dialog.ModalityType.APPLICATION_MODAL);
        this.product = product;
        this.editMode = editMode;
        this.sizes.addAll(catalogController.findSizes(product.getProductId(), true));
        this.toppings.addAll(catalogController.findToppings(true));
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setMinimumSize(new Dimension(720, 720));
        setLocationRelativeTo(owner);
        getContentPane().setBackground(UIConstants.BG_PANEL);
        setLayout(new BorderLayout());
        add(buildContent(), BorderLayout.CENTER);
        javax.swing.JButton cancelButton = UiUtils.secondaryButton("Hủy");
        cancelButton.addActionListener(e -> dispose());
        add(UiUtils.dialogFooter(cancelButton, createConfirmButton()), BorderLayout.SOUTH);
        applyInitialRequest(initial);
        updatePreview();
    }

    public CartLineRequest getResult() {
        return result;
    }

    private javax.swing.JButton createConfirmButton() {
        javax.swing.JButton button = UiUtils.primaryButton(editMode ? "Lưu thay đổi" : "Thêm vào giỏ");
        button.addActionListener(e -> accept());
        return button;
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(0, UIConstants.SPACING_MD));
        root.setBackground(UIConstants.BG_PANEL);
        root.setBorder(new EmptyBorder(UIConstants.SPACING_LG, UIConstants.SPACING_LG,
                UIConstants.SPACING_MD, UIConstants.SPACING_LG));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildOptionsCard(), BorderLayout.CENTER);
        root.add(buildOrderCard(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        header.setBackground(UIConstants.BG_PANEL);
        header.setBorder(cardBorder());
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 126));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel imageLabel = new JLabel(
                imageService.loadThumbnailFitted(product.getImagePath(), 128, 96),
                SwingConstants.CENTER
        );
        imageLabel.setPreferredSize(new Dimension(128, 96));
        header.add(imageLabel, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel title = new JLabel(product.getName());
        title.setFont(UIConstants.fontBold(UIConstants.FONT_SECTION_TITLE));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        String category = product.getCategoryName() == null ? "" : product.getCategoryName();
        JLabel meta = new JLabel(category + "  ·  Tồn kho: " + product.getStockQuantity());
        meta.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        meta.setForeground(product.getStockQuantity() > 0 ? UIConstants.STOCK_OK : UIConstants.STOCK_OUT);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(title);
        info.add(Box.createVerticalStrut(6));
        info.add(meta);
        header.add(info, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildOptionsCard() {
        JPanel card = new JPanel(new BorderLayout(0, UIConstants.SPACING_MD));
        card.setBackground(UIConstants.BG_PANEL);
        card.setBorder(cardBorder());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(buildSizeSection(), BorderLayout.NORTH);
        card.add(buildToppingSection(), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSizeSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(UIConstants.BG_PANEL);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(sectionCaption("Cỡ ly"));
        section.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        section.add(buildSizePanel());
        section.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        section.add(new JSeparator());
        return section;
    }

    private JPanel buildToppingSection() {
        JPanel section = new JPanel(new BorderLayout(0, UIConstants.SPACING_SM));
        section.setBackground(UIConstants.BG_PANEL);
        section.add(sectionCaption("Topping bổ sung"), BorderLayout.NORTH);
        section.add(buildToppingScroll(), BorderLayout.CENTER);
        return section;
    }

    private JPanel buildSizePanel() {
        boolean horizontal = sizes.size() <= 3;
        JPanel panel = horizontal
                ? new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_MD, 0))
                : new JPanel();
        if (!horizontal) {
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        }
        panel.setBackground(UIConstants.BG_PANEL);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(0, UIConstants.SPACING_SM, 0, UIConstants.SPACING_SM));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, UiUtils.optionListHeight(sizes.size())));

        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < sizes.size(); i++) {
            ProductSize size = sizes.get(i);
            JRadioButton button = new JRadioButton(
                    size.getSizeName() + "  ·  " + MoneyUtils.formatVnd(size.getSalePrice()));
            button.setFont(UIConstants.font(UIConstants.FONT_LABEL));
            button.setBackground(UIConstants.BG_PANEL);
            button.setSelected(i == 0);
            button.addActionListener(e -> updatePreview());
            if (!horizontal) {
                button.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            group.add(button);
            sizeButtons.put(button, size);
            panel.add(button);
            if (!horizontal && i < sizes.size() - 1) {
                panel.add(Box.createVerticalStrut(UIConstants.OPTION_ROW_GAP));
            }
        }
        return panel;
    }

    private JScrollPane buildToppingScroll() {
        int columns = UIConstants.TOPPING_GRID_COLUMNS;
        int rowsPerColumn = (int) Math.ceil(toppings.size() / (double) columns);
        rowsPerColumn = Math.max(rowsPerColumn, 1);

        JPanel grid = new JPanel(new GridLayout(1, columns, UIConstants.SPACING_MD, 0));
        grid.setBackground(UIConstants.BG_PANEL);
        grid.setBorder(new EmptyBorder(2, UIConstants.SPACING_SM, 6, UIConstants.SPACING_SM));

        JPanel leftColumn = buildToppingColumn();
        JPanel rightColumn = buildToppingColumn();
        int splitIndex = rowsPerColumn;
        for (int i = 0; i < toppings.size(); i++) {
            JPanel targetColumn = i < splitIndex ? leftColumn : rightColumn;
            targetColumn.add(buildToppingRow(toppings.get(i)));
            if (i != splitIndex - 1 && i != toppings.size() - 1) {
                targetColumn.add(Box.createVerticalStrut(UIConstants.OPTION_ROW_GAP));
            }
        }

        int leftMissingRows = rowsPerColumn - Math.min(rowsPerColumn, toppings.size());
        int rightRows = Math.max(0, toppings.size() - splitIndex);
        int rightMissingRows = rowsPerColumn - rightRows;
        addToppingFillers(leftColumn, leftMissingRows);
        addToppingFillers(rightColumn, rightMissingRows);

        grid.add(leftColumn);
        grid.add(rightColumn);

        int visibleRows = Math.min(UIConstants.TOPPING_GRID_VISIBLE_ROWS, rowsPerColumn);
        int height = UiUtils.optionListHeight(visibleRows) + UIConstants.SPACING_SM;
        int contentHeight = UiUtils.optionListHeight(rowsPerColumn);
        grid.setPreferredSize(new Dimension(0, contentHeight));
        grid.setMinimumSize(new Dimension(0, contentHeight));

        JScrollPane scrollPane = UiUtils.scrollPane(grid, height);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private JPanel buildToppingColumn() {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(UIConstants.BG_PANEL);
        return column;
    }

    private void addToppingFillers(JPanel column, int missingRows) {
        for (int i = 0; i < missingRows; i++) {
            if (column.getComponentCount() > 0) {
                column.add(Box.createVerticalStrut(UIConstants.OPTION_ROW_GAP));
            }
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            empty.setPreferredSize(new Dimension(0, UIConstants.OPTION_ROW_HEIGHT));
            empty.setMinimumSize(new Dimension(0, UIConstants.OPTION_ROW_HEIGHT));
            empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.OPTION_ROW_HEIGHT));
            column.add(empty);
        }
    }

    private JPanel buildToppingRow(Topping topping) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(UIConstants.BG_PANEL);
        Dimension rowSize = new Dimension(0, UIConstants.OPTION_ROW_HEIGHT);
        row.setPreferredSize(rowSize);
        row.setMinimumSize(rowSize);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.OPTION_ROW_HEIGHT));

        JCheckBox box = new JCheckBox(topping.getName());
        box.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        box.setBackground(UIConstants.BG_PANEL);
        box.addActionListener(e -> updatePreview());
        toppingBoxes.put(box, topping);

        JLabel priceLabel = new JLabel("+" + MoneyUtils.formatVnd(topping.getExtraPrice()), SwingConstants.RIGHT);
        priceLabel.setFont(UIConstants.font(UIConstants.FONT_CARD_META));
        priceLabel.setForeground(UIConstants.TEXT_MUTED);

        row.add(box, BorderLayout.CENTER);
        row.add(priceLabel, BorderLayout.EAST);

        MouseAdapter toggle = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                box.setSelected(!box.isSelected());
                updatePreview();
            }
        };
        row.addMouseListener(toggle);
        priceLabel.addMouseListener(toggle);
        return row;
    }

    private JPanel buildOrderCard() {
        UiUtils.styleSpinner(quantitySpinner);
        quantitySpinner.addChangeListener(e -> updatePreview());
        UiUtils.styleField(noteField);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.BG_PANEL);
        card.setBorder(cardBorder());
        card.setPreferredSize(new Dimension(0, 142));
        card.setMinimumSize(new Dimension(0, 142));

        JPanel inputRow = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        inputRow.setOpaque(false);
        inputRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.FORM_FIELD_HEIGHT + 4));

        JPanel qtyPart = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        qtyPart.setOpaque(false);
        qtyPart.add(compactLabel("Số lượng"));
        qtyPart.add(quantitySpinner);
        inputRow.add(qtyPart, BorderLayout.WEST);

        JPanel notePart = new JPanel(new BorderLayout(UIConstants.SPACING_SM, 0));
        notePart.setOpaque(false);
        notePart.add(compactLabel("Ghi chú"), BorderLayout.WEST);
        notePart.add(noteField, BorderLayout.CENTER);
        inputRow.add(notePart, BorderLayout.CENTER);

        card.add(inputRow);
        card.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(UIConstants.SPACING_SM));

        JPanel pricePanel = UiUtils.priceSummaryPanel(unitPriceLabel, lineTotalLabel);
        pricePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(pricePanel);
        return card;
    }

    private void applyInitialRequest(CartLineRequest initial) {
        if (initial == null) {
            return;
        }
        quantitySpinner.setValue(Integer.valueOf(Math.max(1, initial.getQuantity())));
        noteField.setText(initial.getNote() == null ? "" : initial.getNote());

        for (Map.Entry<JRadioButton, ProductSize> entry : sizeButtons.entrySet()) {
            if (entry.getValue().getProductSizeId() == initial.getProductSizeId()) {
                entry.getKey().setSelected(true);
                break;
            }
        }

        Set<Integer> selectedToppings = new HashSet<Integer>(initial.getToppingIds());
        for (Map.Entry<JCheckBox, Topping> entry : toppingBoxes.entrySet()) {
            entry.getKey().setSelected(selectedToppings.contains(Integer.valueOf(entry.getValue().getToppingId())));
        }
    }

    private static JLabel sectionCaption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.fontBold(UIConstants.FONT_LABEL));
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setBorder(new EmptyBorder(0, UIConstants.SPACING_SM, 0, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel compactLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setPreferredSize(new Dimension(88, UIConstants.FORM_FIELD_HEIGHT));
        return label;
    }

    private static javax.swing.border.Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER),
                new EmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_MD,
                        UIConstants.SPACING_MD, UIConstants.SPACING_MD)
        );
    }

    private void updatePreview() {
        ProductSize size = selectedSize();
        if (size == null) {
            return;
        }
        DrinkComponent drink = new BaseDrink(product.getName(), size.getSizeName(), size.getSalePrice());
        for (Map.Entry<JCheckBox, Topping> entry : toppingBoxes.entrySet()) {
            if (entry.getKey().isSelected()) {
                Topping topping = entry.getValue();
                drink = new ToppingDecorator(drink, topping.getName(), topping.getExtraPrice());
            }
        }
        int quantity = ((Integer) quantitySpinner.getValue()).intValue();
        BigDecimal unitPrice = drink.getPrice();
        unitPriceLabel.setText(MoneyUtils.formatVnd(unitPrice));
        lineTotalLabel.setText(MoneyUtils.formatVnd(unitPrice.multiply(BigDecimal.valueOf(quantity))));
    }

    private ProductSize selectedSize() {
        for (Map.Entry<JRadioButton, ProductSize> entry : sizeButtons.entrySet()) {
            if (entry.getKey().isSelected()) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void accept() {
        ProductSize size = selectedSize();
        if (size == null) {
            UiUtils.showInfo(this, "Vui lòng chọn cỡ ly.");
            return;
        }
        CartLineRequest request = new CartLineRequest();
        request.setProductId(product.getProductId());
        request.setProductSizeId(size.getProductSizeId());
        request.setQuantity(((Integer) quantitySpinner.getValue()).intValue());
        request.setNote(noteField.getText());
        List<Integer> toppingIds = new ArrayList<Integer>();
        for (Map.Entry<JCheckBox, Topping> entry : toppingBoxes.entrySet()) {
            if (entry.getKey().isSelected()) {
                toppingIds.add(Integer.valueOf(entry.getValue().getToppingId()));
            }
        }
        request.setToppingIds(toppingIds);
        result = request;
        dispose();
    }
}
