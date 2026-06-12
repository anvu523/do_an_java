package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.CatalogController;
import com.brewpoint.pos.decorator.BaseDrink;
import com.brewpoint.pos.decorator.DrinkComponent;
import com.brewpoint.pos.decorator.ToppingDecorator;
import com.brewpoint.pos.model.CartLineRequest;
import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.model.ProductSize;
import com.brewpoint.pos.model.Topping;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Window;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductOptionDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final Product product;
    private final transient CatalogController catalogController;
    private final List<ProductSize> sizes = new ArrayList<ProductSize>();
    private final List<Topping> toppings = new ArrayList<Topping>();
    private final Map<JRadioButton, ProductSize> sizeButtons = new LinkedHashMap<JRadioButton, ProductSize>();
    private final Map<JCheckBox, Topping> toppingBoxes = new LinkedHashMap<JCheckBox, Topping>();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JTextArea noteArea = new JTextArea(3, 24);
    private final JLabel unitPriceLabel = new JLabel("0 ₫");
    private final JLabel lineTotalLabel = new JLabel("0 ₫");
    private CartLineRequest result;

    public ProductOptionDialog(Window owner, Product product, CatalogController catalogController) throws SQLException {
        super(owner, "Chọn món", Dialog.ModalityType.APPLICATION_MODAL);
        this.product = product;
        this.catalogController = catalogController;
        this.sizes.addAll(catalogController.findSizes(product.getProductId(), true));
        this.toppings.addAll(catalogController.findToppings(true));
        setSize(520, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        add(buildContent(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        updatePreview();
    }

    public CartLineRequest getResult() {
        return result;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 12, 0));
        JPanel left = new JPanel(new BorderLayout());
        JLabel title = new JLabel("<html><h2>" + product.getName() + "</h2></html>");
        left.add(title, BorderLayout.NORTH);
        left.add(new JLabel("Tồn kho: " + product.getStockQuantity()), BorderLayout.CENTER);
        content.add(left);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.add(buildSizePanel(), BorderLayout.NORTH);
        right.add(buildToppingPanel(), BorderLayout.CENTER);
        right.add(buildQuantityPanel(), BorderLayout.SOUTH);
        content.add(right);
        return content;
    }

    private JPanel buildSizePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        UiUtils.panelBorder(panel, "Size");
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < sizes.size(); i++) {
            ProductSize size = sizes.get(i);
            JRadioButton button = new JRadioButton(size.getSizeName() + " - " + MoneyUtils.formatVnd(size.getSalePrice()));
            button.setSelected(i == 0);
            button.addActionListener(e -> updatePreview());
            group.add(button);
            sizeButtons.put(button, size);
            panel.add(button);
        }
        return panel;
    }

    private JScrollPane buildToppingPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        UiUtils.panelBorder(panel, "Topping");
        for (Topping topping : toppings) {
            JCheckBox box = new JCheckBox(topping.getName() + " +" + MoneyUtils.formatVnd(topping.getExtraPrice()));
            box.addActionListener(e -> updatePreview());
            toppingBoxes.put(box, topping);
            panel.add(box);
        }
        return new JScrollPane(panel);
    }

    private JPanel buildQuantityPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Số lượng"));
        quantitySpinner.addChangeListener(e -> updatePreview());
        panel.add(quantitySpinner);
        panel.add(new JLabel("Ghi chú"));
        panel.add(new JScrollPane(noteArea));
        panel.add(new JLabel("Đơn giá"));
        panel.add(unitPriceLabel);
        panel.add(new JLabel("Thành tiền"));
        panel.add(lineTotalLabel);
        return panel;
    }

    private JPanel buildBottom() {
        JPanel bottom = new JPanel();
        javax.swing.JButton cancelButton = new javax.swing.JButton("Hủy");
        cancelButton.addActionListener(e -> dispose());
        javax.swing.JButton addButton = UiUtils.primaryButton("Thêm vào giỏ");
        addButton.addActionListener(e -> accept());
        bottom.add(cancelButton);
        bottom.add(addButton);
        return bottom;
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
            UiUtils.showInfo(this, "Chọn size.");
            return;
        }
        CartLineRequest request = new CartLineRequest();
        request.setProductId(product.getProductId());
        request.setProductSizeId(size.getProductSizeId());
        request.setQuantity(((Integer) quantitySpinner.getValue()).intValue());
        request.setNote(noteArea.getText());
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
