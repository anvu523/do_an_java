package com.brewpoint.pos.util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public final class UiUtils {
    public static final Color BACKGROUND = UIConstants.BG_APP;
    public static final Color PRIMARY = UIConstants.PRIMARY;
    public static final Color BORDER = UIConstants.BORDER;

    private UiUtils() {
    }

    public static void showError(Component parent, Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "Đã xảy ra lỗi. Vui lòng thử lại.";
        }
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static JButton primaryButton(String text) {
        return styledButton(text, UIConstants.PRIMARY, UIConstants.PRIMARY_DARK, UIConstants.TEXT_INVERSE);
    }

    public static JButton secondaryButton(String text) {
        return styledButton(text, UIConstants.BG_PANEL, UIConstants.BORDER, UIConstants.TEXT_PRIMARY);
    }

    public static void setNavButtonSelected(JButton button, boolean selected) {
        if (selected) {
            button.setFont(UIConstants.fontBold(UIConstants.FONT_BUTTON));
            button.setForeground(UIConstants.PRIMARY);
        } else {
            button.setFont(UIConstants.font(UIConstants.FONT_BUTTON));
            button.setForeground(UIConstants.TEXT_PRIMARY);
        }
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, UIConstants.DANGER, UIConstants.DANGER_DARK, UIConstants.TEXT_INVERSE);
    }

    public static void styleLabel(JLabel label) {
        label.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        label.setForeground(UIConstants.TEXT_PRIMARY);
    }

    public static void styleSectionTitle(JLabel label) {
        label.setFont(UIConstants.fontBold(UIConstants.FONT_SECTION_TITLE));
        label.setForeground(UIConstants.TEXT_PRIMARY);
    }

    public static void styleField(JTextField field) {
        field.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        int width = field.getPreferredSize().width;
        if (width < 140) {
            width = 140;
        }
        field.setPreferredSize(new Dimension(width, UIConstants.FORM_FIELD_HEIGHT));
    }

    public static void styleCompactField(JTextField field) {
        field.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        int cols = field.getColumns();
        int width = cols > 0 ? cols * 9 + 24 : 100;
        Dimension size = new Dimension(width, UIConstants.FORM_FIELD_HEIGHT);
        field.setPreferredSize(size);
        field.setMinimumSize(size);
        field.setMaximumSize(size);
    }

    public static void installPlaceholder(JTextField field, final String placeholder) {
        field.putClientProperty("brewpoint.placeholder", placeholder);
        field.setForeground(UIConstants.TEXT_MUTED);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (placeholder.equals(field.getText())) {
                    field.setText("");
                    field.setForeground(UIConstants.TEXT_PRIMARY);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText() == null || field.getText().trim().isEmpty()) {
                    field.setForeground(UIConstants.TEXT_MUTED);
                    field.setText(placeholder);
                }
            }
        });
    }

    public static String readFieldText(JTextField field) {
        Object placeholder = field.getClientProperty("brewpoint.placeholder");
        String text = field.getText();
        if (text == null) {
            return "";
        }
        if (placeholder != null && placeholder.equals(text)) {
            return "";
        }
        return text;
    }

    public static JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        spinner.setPreferredSize(new Dimension(100, UIConstants.FORM_FIELD_HEIGHT));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setFont(UIConstants.font(UIConstants.FONT_INPUT));
            textField.setPreferredSize(new Dimension(80, UIConstants.FORM_FIELD_HEIGHT));
        }
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setRows(UIConstants.FORM_TEXTAREA_ROWS);
    }

    public static JScrollPane scrollPane(JComponent view, int height) {
        return scrollPane(view, height, true);
    }

    public static JScrollPane scrollPaneBorderless(JComponent view, int height) {
        return scrollPane(view, height, false);
    }

    private static JScrollPane scrollPane(JComponent view, int height, boolean bordered) {
        JScrollPane scrollPane = new JScrollPane(view);
        if (bordered) {
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        } else {
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        }
        Dimension size = new Dimension(0, height);
        scrollPane.setPreferredSize(size);
        scrollPane.setMinimumSize(size);
        scrollPane.getViewport().setBackground(UIConstants.BG_PANEL);
        scrollPane.setBackground(UIConstants.BG_PANEL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(UIConstants.OPTION_ROW_HEIGHT);
        return scrollPane;
    }

    public static JPanel sectionPanel(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout(UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        panel.setBackground(UIConstants.BG_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                UIConstants.fontBold(UIConstants.FONT_LABEL),
                UIConstants.TEXT_PRIMARY
        ));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    public static JPanel sectionPanel(String title, JComponent content, int minContentHeight) {
        JPanel panel = sectionPanel(title, content);
        int sectionHeight = minContentHeight + 32;
        Dimension size = new Dimension(0, sectionHeight);
        panel.setMinimumSize(size);
        panel.setPreferredSize(size);
        return panel;
    }

    public static int optionListHeight(int rowCount) {
        int rows = Math.max(1, rowCount);
        return rows * UIConstants.OPTION_ROW_HEIGHT
                + Math.max(0, rows - 1) * UIConstants.OPTION_ROW_GAP
                + UIConstants.SPACING_SM;
    }

    public static int toppingListViewportHeight() {
        return optionListHeight(UIConstants.TOPPING_VISIBLE_ROWS) + UIConstants.SPACING_MD;
    }

    public static int toppingGridViewportHeight() {
        return optionListHeight(UIConstants.TOPPING_GRID_VISIBLE_ROWS) + UIConstants.SPACING_SM;
    }

    public static JPanel priceSummaryPanel(JLabel unitPriceLabel, JLabel lineTotalLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(UIConstants.SPACING_SM, 0, 0, 0));
        panel.setPreferredSize(new Dimension(0, 52));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        left.setOpaque(false);
        JLabel unitCaption = new JLabel("Đơn giá:");
        styleLabel(unitCaption);
        unitPriceLabel.setFont(UIConstants.fontBold(UIConstants.FONT_LABEL));
        unitPriceLabel.setForeground(UIConstants.TEXT_PRIMARY);
        left.add(unitCaption);
        left.add(unitPriceLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        right.setOpaque(false);
        JLabel totalCaption = new JLabel("Thành tiền:");
        styleLabel(totalCaption);
        lineTotalLabel.setFont(UIConstants.fontBold(UIConstants.FONT_TOTAL));
        lineTotalLabel.setForeground(UIConstants.PRIMARY);
        right.add(totalCaption);
        right.add(lineTotalLabel);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    public static JPanel wrapFormCard(JPanel formBody, JButton... actions) {
        JPanel card = new JPanel(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        card.setBackground(UIConstants.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_MD,
                        UIConstants.SPACING_MD, UIConstants.SPACING_MD)
        ));
        card.add(formBody, BorderLayout.CENTER);
        if (actions != null && actions.length > 0) {
            card.add(actionBar(actions), BorderLayout.SOUTH);
        }
        return card;
    }

    public static JPanel actionBar(JButton... actions) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        bar.setOpaque(false);
        for (int i = 0; i < actions.length; i++) {
            bar.add(actions[i]);
        }
        return bar;
    }

    public static JPanel dialogFooter(JButton cancelButton, JButton primaryButton) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        footer.setBackground(UIConstants.BG_PANEL);
        footer.setBorder(new EmptyBorder(UIConstants.SPACING_SM, UIConstants.SPACING_LG,
                UIConstants.SPACING_MD, UIConstants.SPACING_LG));
        if (cancelButton != null) {
            footer.add(cancelButton);
        }
        if (primaryButton != null) {
            footer.add(primaryButton);
        }
        return footer;
    }

    public static void styleContentPanel(JPanel panel) {
        panel.setBackground(UIConstants.BG_APP);
        panel.setBorder(new EmptyBorder(UIConstants.CONTENT_PADDING, UIConstants.CONTENT_PADDING,
                UIConstants.CONTENT_PADDING, UIConstants.CONTENT_PADDING));
    }

    public static void styleFormPanel(JPanel panel) {
        panel.setBackground(UIConstants.BG_APP);
        panel.setBorder(new EmptyBorder(UIConstants.SPACING_SM, 0, UIConstants.SPACING_SM, 0));
    }

    public static void styleDialogContent(JComponent component) {
        component.setBorder(new EmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_LG,
                UIConstants.SPACING_MD, UIConstants.SPACING_LG));
        component.setBackground(UIConstants.BG_PANEL);
    }

    public static int requiredTableWidth(JTable table, String[] headers, int[] minWidths, int extraPadding) {
        JTableHeader header = table.getTableHeader();
        java.awt.FontMetrics metrics = header.getFontMetrics(header.getFont());
        int total = extraPadding;
        for (int i = 0; i < headers.length; i++) {
            int width = Math.max(minWidths[i], metrics.stringWidth(headers[i]) + 20);
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
            total += width;
        }
        return total;
    }

    public static void configureTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        table.setFont(UIConstants.font(UIConstants.FONT_TABLE));
        table.setForeground(UIConstants.TEXT_PRIMARY);
        table.setGridColor(UIConstants.BORDER);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(8, 1));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(UIConstants.fontBold(UIConstants.FONT_TABLE));
        header.setBackground(UIConstants.TABLE_HEADER_BG);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, UIConstants.TABLE_ROW_HEIGHT + 4));
    }

    public static void panelBorder(JComponent component, String title) {
        component.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                UIConstants.fontBold(UIConstants.FONT_SECTION_TITLE),
                UIConstants.TEXT_PRIMARY
        ));
    }

    public static Border emptyBorder(int size) {
        return new EmptyBorder(size, size, size, size);
    }

    private static JButton styledButton(String text, Color background, Color pressed, Color foreground) {
        JButton button = new JButton(text) {
            private static final long serialVersionUID = 1L;

            protected void paintComponent(Graphics graphics) {
                if (isEnabled()) {
                    if (getModel().isPressed()) {
                        setBackground(pressed);
                    } else if (getModel().isRollover()) {
                        setBackground(background.brighter());
                    } else {
                        setBackground(background);
                    }
                } else {
                    setBackground(UIConstants.CARD_DISABLED_BG);
                    setForeground(UIConstants.TEXT_MUTED);
                }
                super.paintComponent(graphics);
            }
        };
        button.setFont(UIConstants.fontBold(UIConstants.FONT_BUTTON));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setUI(new BasicButtonUI());
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(pressed, 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        button.setPreferredSize(new Dimension(Math.max(120, button.getPreferredSize().width), 44));
        return button;
    }
}
