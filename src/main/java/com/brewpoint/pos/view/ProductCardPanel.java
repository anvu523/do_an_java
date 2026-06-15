package com.brewpoint.pos.view;

import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.util.ImageService;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProductCardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int LOW_STOCK_THRESHOLD = 10;

    private final Product product;
    private final ImageService imageService;
    private final Runnable onSelect;
    private final JLabel imageLabel;
    private final boolean selectable;
    private boolean selected;
    private boolean hovered;
    private int imageWidth = UIConstants.CARD_IMAGE_WIDTH;
    private int imageHeight = UIConstants.CARD_IMAGE_HEIGHT;

    public ProductCardPanel(Product product, ImageService imageService, Runnable onSelect) {
        this.product = product;
        this.imageService = imageService;
        this.onSelect = onSelect;
        selectable = product.getStockQuantity() > 0 && product.getFromPrice() != null;
        setLayout(new BorderLayout(UIConstants.CARD_PADDING, UIConstants.CARD_PADDING));
        applyCardSize(UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT);
        setOpaque(true);
        applyVisualState();

        imageLabel = new JLabel(loadImageIcon(), SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(imageWidth, imageHeight));
        add(imageLabel, BorderLayout.NORTH);
        add(buildInfoPanel(), BorderLayout.SOUTH);

        if (selectable && onSelect != null) {
            wireSelectionHandlers();
        }
    }

    private JPanel buildInfoPanel() {
        int stock = product.getStockQuantity();
        boolean showStock = stock <= 0 || stock < LOW_STOCK_THRESHOLD;
        int rows = showStock ? 3 : 2;
        JPanel info = new JPanel(new GridLayout(rows, 1, 0, 4));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel("<html><div style='text-align:center'>"
                + escapeHtml(product.getName()) + "</div></html>", SwingConstants.CENTER);
        nameLabel.setFont(UIConstants.fontBold(UIConstants.FONT_CARD_NAME));
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        info.add(nameLabel);

        String priceText = product.getFromPrice() == null ? "Chưa có giá" : MoneyUtils.formatVnd(product.getFromPrice());
        JLabel priceLabel = new JLabel(priceText, SwingConstants.CENTER);
        priceLabel.setFont(UIConstants.fontBold(UIConstants.FONT_CARD_META));
        priceLabel.setForeground(UIConstants.PRIMARY);
        info.add(priceLabel);

        if (showStock) {
            String stockText = stock > 0 ? "Còn " + stock : "Tạm hết";
            JLabel stockLabel = new JLabel(stockText, SwingConstants.CENTER);
            stockLabel.setFont(UIConstants.font(UIConstants.FONT_CARD_META));
            stockLabel.setForeground(stock > 0 ? UIConstants.STOCK_OK : UIConstants.STOCK_OUT);
            info.add(stockLabel);
        }

        return info;
    }

    private void wireSelectionHandlers() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter adapter = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }
                setSelected(true);
                onSelect.run();
            }

            public void mouseEntered(MouseEvent e) {
                hovered = true;
                applyVisualState();
            }

            public void mouseExited(MouseEvent e) {
                hovered = false;
                applyVisualState();
            }
        };
        installListener(this, adapter);
    }

    /**
     * Mỗi component con chỉ gắn một listener — tránh gọi onSelect hai lần mỗi click.
     */
    private void installListener(Component component, MouseAdapter adapter) {
        component.addMouseListener(adapter);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                installListener(container.getComponent(i), adapter);
            }
        }
    }

    public void setCardSize(int width, int height, int newImageWidth, int newImageHeight) {
        imageWidth = newImageWidth;
        imageHeight = newImageHeight;
        applyCardSize(width, height);
        imageLabel.setIcon(loadImageIcon());
        imageLabel.setPreferredSize(new Dimension(imageWidth, imageHeight));
        revalidate();
        repaint();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        applyVisualState();
    }

    public void clearSelected() {
        setSelected(false);
    }

    private void applyCardSize(int width, int height) {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }

    private javax.swing.ImageIcon loadImageIcon() {
        return imageService.loadThumbnailFitted(product.getImagePath(), imageWidth, imageHeight);
    }

    private void applyVisualState() {
        Color background;
        Color borderColor;
        int borderWidth;
        if (!selectable) {
            background = UIConstants.CARD_DISABLED_BG;
            borderColor = UIConstants.BORDER;
            borderWidth = 1;
        } else if (selected) {
            background = UIConstants.CARD_BG;
            borderColor = UIConstants.CARD_SELECTED_BORDER;
            borderWidth = 2;
        } else if (hovered) {
            background = UIConstants.CARD_BG;
            borderColor = UIConstants.CARD_HOVER_BORDER;
            borderWidth = 2;
        } else {
            background = UIConstants.CARD_BG;
            borderColor = UIConstants.BORDER;
            borderWidth = 1;
        }
        setBackground(background);
        setBorder(BorderFactory.createLineBorder(borderColor, borderWidth));
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
