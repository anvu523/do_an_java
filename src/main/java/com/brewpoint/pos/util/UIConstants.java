package com.brewpoint.pos.util;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

public final class UIConstants {
    public static final String FONT_FAMILY = resolveFontFamily();

    public static final float FONT_MAIN_TITLE = 30f;
    public static final float FONT_SECTION_TITLE = 22f;
    public static final float FONT_BUTTON = 18f;
    public static final float FONT_TABLE = 17f;
    public static final float FONT_LABEL = 17f;
    public static final float FONT_INPUT = 17f;
    public static final float FONT_TOTAL = 32f;
    public static final float FONT_CARD_NAME = 16f;
    public static final float FONT_CARD_META = 15f;
    public static final float FONT_METRIC_VALUE = 24f;

    public static final Color BG_APP = new Color(246, 247, 249);
    public static final Color BG_PANEL = Color.WHITE;
    public static final Color BG_SIDEBAR = new Color(242, 244, 247);
    public static final Color PRIMARY = new Color(36, 99, 71);
    public static final Color PRIMARY_DARK = new Color(28, 78, 56);
    public static final Color DANGER = new Color(183, 52, 52);
    public static final Color DANGER_DARK = new Color(150, 40, 40);
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_MUTED = new Color(82, 91, 103);
    public static final Color TEXT_INVERSE = Color.WHITE;
    public static final Color BORDER = new Color(210, 214, 220);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_DISABLED_BG = new Color(238, 240, 243);
    public static final Color CARD_HOVER_BORDER = new Color(56, 130, 95);
    public static final Color CARD_SELECTED_BORDER = new Color(24, 88, 62);
    public static final Color TABLE_HEADER_BG = new Color(236, 240, 244);
    public static final Color TABLE_SELECTION_BG = new Color(214, 236, 224);
    public static final Color PLACEHOLDER_BG = new Color(232, 236, 240);
    public static final Color STOCK_OK = new Color(36, 99, 71);
    public static final Color STOCK_OUT = new Color(183, 52, 52);

    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 12;
    public static final int SPACING_LG = 16;
    public static final int CONTENT_PADDING = 12;

    public static final int TABLE_ROW_HEIGHT = 40;
    public static final int CARD_WIDTH = 220;
    public static final int CARD_HEIGHT = 290;
    public static final int CARD_IMAGE_WIDTH = 196;
    public static final int CARD_IMAGE_HEIGHT = 148;
    public static final int CARD_PADDING = 10;
    public static final int CARD_GAP = 12;
    public static final int SIDEBAR_WIDTH = 230;
    public static final int CART_MIN_WIDTH = 520;
    public static final int FORM_FIELD_HEIGHT = 36;
    public static final int FORM_TEXTAREA_ROWS = 2;
    public static final int TOPPING_VISIBLE_ROWS = 7;
    public static final int TOPPING_GRID_COLUMNS = 2;
    public static final int TOPPING_GRID_VISIBLE_ROWS = 6;
    public static final int OPTION_ROW_HEIGHT = 36;
    public static final int OPTION_ROW_GAP = 2;
    public static final double CART_WIDTH_RATIO = 0.45d;
    public static final int PRODUCT_GRID_MAX_COLUMNS = 3;

    private UIConstants() {
    }

    public static Font font(float size) {
        return new Font(FONT_FAMILY, Font.PLAIN, Math.round(size));
    }

    public static Font fontBold(float size) {
        return new Font(FONT_FAMILY, Font.BOLD, Math.round(size));
    }

    public static int columnsForWidth(int width) {
        if (width < 1000) {
            return 2;
        }
        if (width < 1280) {
            return 3;
        }
        return PRODUCT_GRID_MAX_COLUMNS;
    }

    public static int columnsForViewport(int viewportWidth) {
        return cardLayoutForViewport(viewportWidth)[0];
    }

    public static int cardWidthForViewport(int viewportWidth) {
        return cardLayoutForViewport(viewportWidth)[1];
    }

    public static int[] cardLayoutForViewport(int viewportWidth) {
        int defaultColumns = 3;
        int defaultWidth = CARD_WIDTH;
        if (viewportWidth <= 0) {
            return new int[]{defaultColumns, defaultWidth};
        }
        int gap = CARD_GAP;
        int minCardWidth = 175;
        int columns = 2;
        int cardWidth = minCardWidth;
        for (int cols = PRODUCT_GRID_MAX_COLUMNS; cols >= 2; cols--) {
            int available = viewportWidth - gap * (cols + 1);
            int width = available / cols;
            if (width >= minCardWidth) {
                columns = cols;
                cardWidth = width;
                break;
            }
        }
        if (columns == 2 && cardWidth == minCardWidth) {
            int available = viewportWidth - gap * 3;
            cardWidth = Math.max(minCardWidth, available / 2);
        }
        return new int[]{columns, cardWidth};
    }

    public static void applyGlobalLookAndFeel() {
        UIManager.put("Button.font", fontBold(FONT_BUTTON));
        UIManager.put("Label.font", font(FONT_LABEL));
        UIManager.put("TextField.font", font(FONT_INPUT));
        UIManager.put("PasswordField.font", font(FONT_INPUT));
        UIManager.put("ComboBox.font", font(FONT_INPUT));
        UIManager.put("Table.font", font(FONT_TABLE));
        UIManager.put("TableHeader.font", fontBold(FONT_TABLE));
        UIManager.put("RadioButton.font", font(FONT_LABEL));
        UIManager.put("CheckBox.font", font(FONT_LABEL));
        UIManager.put("Spinner.font", font(FONT_INPUT));
        UIManager.put("TextArea.font", font(FONT_INPUT));
        UIManager.put("TitledBorder.font", fontBold(FONT_SECTION_TITLE));
        UIManager.put("OptionPane.messageFont", font(FONT_LABEL));
        UIManager.put("OptionPane.buttonFont", fontBold(FONT_BUTTON));
        UIManager.put("Panel.background", BG_APP);
        UIManager.put("Viewport.background", BG_APP);
        UIManager.put("Table.rowHeight", Integer.valueOf(TABLE_ROW_HEIGHT));
        UIManager.put("Table.selectionBackground", TABLE_SELECTION_BG);
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
        UIManager.put("TableHeader.background", TABLE_HEADER_BG);
        UIManager.put("TableHeader.foreground", TEXT_PRIMARY);
        UIManager.put("ScrollBar.width", Integer.valueOf(14));
    }

    private static String resolveFontFamily() {
        String[] preferred = new String[]{"Segoe UI", "Inter", "Dialog", Font.SANS_SERIF};
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (int i = 0; i < preferred.length; i++) {
            for (int j = 0; j < available.length; j++) {
                if (preferred[i].equalsIgnoreCase(available[j])) {
                    return available[j];
                }
            }
        }
        return Font.SANS_SERIF;
    }
}
