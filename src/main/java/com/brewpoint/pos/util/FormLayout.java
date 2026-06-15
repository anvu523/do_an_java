package com.brewpoint.pos.util;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public final class FormLayout {
    private final JPanel panel = new JPanel(new GridBagLayout());
    private final GridBagConstraints labelConstraints = new GridBagConstraints();
    private final GridBagConstraints fieldConstraints = new GridBagConstraints();
    private int row;

    public FormLayout() {
        UiUtils.styleFormPanel(panel);
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(6, 0, 6, UIConstants.SPACING_SM);
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0d;
        fieldConstraints.insets = new Insets(6, 0, 6, UIConstants.SPACING_MD);
    }

    public FormLayout addRow(String labelText, JComponent field) {
        JLabel label = UiUtils.formLabel(labelText);
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.weightx = 0.0d;
        labelConstraints.fill = GridBagConstraints.NONE;
        panel.add(label, labelConstraints);

        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1.0d;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, fieldConstraints);
        row++;
        return this;
    }

    public FormLayout addFullWidth(JComponent component) {
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.gridwidth = 2;
        labelConstraints.weightx = 1.0d;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, labelConstraints);
        labelConstraints.gridwidth = 1;
        row++;
        return this;
    }

    public JPanel build() {
        return panel;
    }

    public JPanel buildCard(javax.swing.JButton... actions) {
        return UiUtils.wrapFormCard(panel, actions);
    }
}
