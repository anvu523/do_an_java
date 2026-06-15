package com.brewpoint.pos.view;

import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.WrapLayout;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;

public class ProductGridPanel extends JPanel implements Scrollable {
    private static final long serialVersionUID = 1L;

    public ProductGridPanel() {
        setBackground(UIConstants.BG_APP);
        setLayout(new WrapLayout());
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 24;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return visibleRect == null ? 24 : visibleRect.height;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
