package com.brewpoint.pos.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * FlowLayout variant that wraps components and centers each row.
 */
public class WrapLayout extends FlowLayout {
    private static final long serialVersionUID = 1L;

    public WrapLayout() {
        super(CENTER, UIConstants.CARD_GAP, UIConstants.CARD_GAP);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxWidth = resolveTargetWidth(target) - insets.left - insets.right;
            if (maxWidth <= 0) {
                return;
            }
            List<Component> visible = collectVisible(target);
            List<List<Component>> rows = buildRows(visible, maxWidth, true);
            int y = insets.top + getVgap();
            for (int i = 0; i < rows.size(); i++) {
                List<Component> row = rows.get(i);
                int rowHeight = rowHeight(row, true);
                int rowWidth = rowWidth(row, true);
                int x = insets.left + Math.max(0, (maxWidth - rowWidth) / 2);
                for (int j = 0; j < row.size(); j++) {
                    Component component = row.get(j);
                    Dimension size = component.getPreferredSize();
                    component.setBounds(x, y, size.width, size.height);
                    x += size.width + getHgap();
                }
                y += rowHeight + getVgap();
            }
        }
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxWidth = resolveTargetWidth(target) - insets.left - insets.right;
            if (maxWidth <= 0) {
                maxWidth = 800;
            }
            List<Component> visible = collectVisible(target);
            List<List<Component>> rows = buildRows(visible, maxWidth, preferred);
            int contentWidth = 0;
            int height = getVgap();
            for (int i = 0; i < rows.size(); i++) {
                List<Component> row = rows.get(i);
                contentWidth = Math.max(contentWidth, rowWidth(row, preferred));
                height += rowHeight(row, preferred);
                if (i < rows.size() - 1) {
                    height += getVgap();
                }
            }
            return new Dimension(
                    Math.max(maxWidth, contentWidth) + insets.left + insets.right,
                    height + insets.top + insets.bottom + getVgap()
            );
        }
    }

    private int resolveTargetWidth(Container target) {
        int targetWidth = target.getWidth();
        if (targetWidth <= 0 && target.getParent() instanceof javax.swing.JViewport) {
            targetWidth = target.getParent().getWidth();
        }
        if (targetWidth <= 0) {
            targetWidth = 800;
        }
        return targetWidth;
    }

    private List<Component> collectVisible(Container target) {
        List<Component> visible = new ArrayList<Component>();
        Component[] components = target.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i].isVisible()) {
                visible.add(components[i]);
            }
        }
        return visible;
    }

    private List<List<Component>> buildRows(List<Component> visible, int maxWidth, boolean preferred) {
        List<List<Component>> rows = new ArrayList<List<Component>>();
        List<Component> currentRow = new ArrayList<Component>();
        int rowWidth = 0;
        for (int i = 0; i < visible.size(); i++) {
            Component component = visible.get(i);
            Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
            if (!currentRow.isEmpty() && rowWidth + getHgap() + size.width > maxWidth) {
                rows.add(currentRow);
                currentRow = new ArrayList<Component>();
                rowWidth = 0;
            }
            currentRow.add(component);
            rowWidth += size.width;
            if (currentRow.size() > 1) {
                rowWidth += getHgap();
            }
        }
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }
        return rows;
    }

    private int rowWidth(List<Component> row, boolean preferred) {
        int width = 0;
        for (int i = 0; i < row.size(); i++) {
            Dimension size = preferred ? row.get(i).getPreferredSize() : row.get(i).getMinimumSize();
            width += size.width;
            if (i > 0) {
                width += getHgap();
            }
        }
        return width;
    }

    private int rowHeight(List<Component> row, boolean preferred) {
        int height = 0;
        for (int i = 0; i < row.size(); i++) {
            Dimension size = preferred ? row.get(i).getPreferredSize() : row.get(i).getMinimumSize();
            height = Math.max(height, size.height);
        }
        return height;
    }
}
