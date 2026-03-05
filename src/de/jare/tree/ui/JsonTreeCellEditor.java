/* <copyright> 
 * Copyright (c) 2025, Janusch Rentenatus. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * </copyright>
 */

package de.jare.tree.ui;

import de.jare.tree.data.JsonTreeNodeData;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.AbstractCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;

public class JsonTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {

    private final JTextField textField = new JTextField();
    private JsonTreeNodeData currentData;

    public JsonTreeCellEditor() {
        // Optional: Grundspaltenzahl, falls Metrics noch nicht da sind
        textField.setColumns(10);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFieldSize();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFieldSize();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFieldSize();
            }
        });

    }

    @Override
    public Component getTreeCellEditorComponent(
            JTree tree, Object value, boolean isSelected,
            boolean expanded, boolean leaf, int row) {

        currentData = null;
        if (value instanceof DefaultMutableTreeNode dmtn
                && dmtn.getUserObject() instanceof JsonTreeNodeData d) {
            currentData = d;
            textField.setText(d.getEditText());
            textField.setForeground(d.getForecolor());
        } else {
            textField.setText("");
            textField.setForeground(tree.getForeground());
        }

        updateFieldSize();

        return textField;
    }

    private void updateFieldSize() {
        String text = textField.getText();
        if (text == null) {
            text = "";
        }

        FontMetrics fm = textField.getFontMetrics(textField.getFont());
        // Breite des Textes + etwas Padding
        int textWidth = fm.stringWidth(text) + 10;

        // Mindestbreite: 10 Zeichen oder 128px, je nachdem was gr��er ist
        int min10Chars = Math.max(textWidth, fm.charWidth('M') * 10);
        int minWidth = Math.max(min10Chars, 128);

        int width = Math.max(textWidth, minWidth);
        Dimension size = textField.getPreferredSize();
        size.width = width;
        textField.setPreferredSize(size);
        textField.setMinimumSize(size);
        textField.setSize(size);
    }

    @Override
    public Object getCellEditorValue() {
        if (currentData != null) {
            currentData.setEditText(textField.getText());
            return currentData;
        }
        return textField.getText();
    }
}
