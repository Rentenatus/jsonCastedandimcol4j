/* <copyright> 
 * Copyright (c) 2025, Janusch Rentenatus. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * </copyright>
 */

package de.jare.tree.ui;

import de.jare.tree.control.MasterControl;
import de.jare.tree.data.JsonTreeNodeData;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class WoodEditPopup extends JPopupMenu {

    public WoodEditPopup(MasterControl master) {
        JMenuItem addNodeItem = new JMenuItem("Node hinzufÃ¼gen");
        JMenuItem deleteNodeItem = new JMenuItem("Node lÃ¶schen");
        JMenuItem renameNodeItem = new JMenuItem("Node umbenennen");

        addNodeItem.addActionListener(e -> master.fireCommand("edit.addNode"));
        deleteNodeItem.addActionListener(e -> master.fireCommand("edit.deleteNode"));
        renameNodeItem.addActionListener(e -> master.fireCommand("edit.renameNode"));

        add(addNodeItem);
        add(deleteNodeItem);
        add(renameNodeItem);

        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem pasteItem = new JMenuItem("Paste");

        copyItem.addActionListener(e -> master.fireCommand("edit.copy", master.getClipboardTree()));
        cutItem.addActionListener(e -> master.fireCommand("edit.cut", master.getClipboardTree()));
        pasteItem.addActionListener(e -> master.fireCommand("edit.paste", master.getClipboardTree()));

        addSeparator();
        add(copyItem);
        add(cutItem);
        add(pasteItem);

        master.addSelectionListener(new de.jare.tree.control.listeners.SelectionListener() {
            @Override
            public void onNodeSelected(Object node, Object... payload) {
                boolean rootSelected = false;
                if (payload != null && payload.length > 0 && payload[0] instanceof Boolean b) {
                    rootSelected = b;
                }
                boolean enableCutDelete = !rootSelected && node != null;
                deleteNodeItem.setEnabled(enableCutDelete);
                cutItem.setEnabled(enableCutDelete);

                boolean canPaste = false;
                if (node instanceof DefaultMutableTreeNode dmtn) {
                    Object uo = dmtn.getUserObject();
                    if (uo instanceof JsonTreeNodeData targetData) {
                        canPaste = master.getClipboardTree().canPasteTo(targetData);
                    }
                }
                pasteItem.setEnabled(canPaste);
            }

            @Override
            public void onEditorSelected(Object editor, Object... payload) {
                // optional: Menü bei Editorwechsel anpassen
            }
        });
    }

    /**
     * Hilfsmethode, um das Popup an einem JTree zu registrieren.
     * @param tree
     * @param popup
     */
    public static void installOn(JTree tree, WoodEditPopup popup) {
        tree.addMouseListener(new MouseAdapter() {
            private void showIfPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tree.getRowForLocation(e.getX(), e.getY());
                    if (row != -1) {
                        tree.setSelectionRow(row);
                    }
                    popup.show(tree, e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showIfPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showIfPopup(e);
            }
        });
    }
}
