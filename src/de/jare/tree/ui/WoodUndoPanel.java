/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.jare.tree.ui;

import de.jare.tree.control.MasterControl;
import de.jare.tree.control.UndoManager;
import de.jare.tree.control.commands.WoodCommand;
import de.jare.tree.control.listeners.UndoRedoListener;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.tree.TreeModel;

public class WoodUndoPanel extends JPanel implements UndoRedoListener {

    private final UndoTableModel undoModel;
    private final JTable undoTable;

    public WoodUndoPanel(MasterControl master) {
        super(new BorderLayout());

        undoModel = new UndoTableModel();
        undoTable = new JTable(undoModel);

        // Benutzer-Selektion deaktivieren, aber programmatisch m�glich
        undoTable.setRowSelectionAllowed(false);
        undoTable.setColumnSelectionAllowed(false);
        undoTable.setCellSelectionEnabled(false);
        undoTable.setFocusable(false);

        add(new JScrollPane(undoTable), BorderLayout.CENTER);

        master.addUndoRedoListener(this);
    }

    @Override
    public void onAddCommand(TreeModel tm, WoodCommand cmd) {
        undoModel.addCommand(cmd);
        selectCurrent();
    }

    @Override
    public void onUndo(TreeModel tm, WoodCommand cmd) {
        undoModel.markUndo();
        selectCurrent();
    }

    @Override
    public void onRedo(TreeModel tm, WoodCommand cmd) {
        undoModel.markRedo();
        selectCurrent();
    }

    private void selectCurrent() {
        int idx = undoModel.getSelectedIndex();
        if (idx >= 0) {
            // obwohl Selection f�r User aus ist, geht das programmatisch
            undoTable.getSelectionModel().setSelectionInterval(idx, idx);
            undoTable.scrollRectToVisible(undoTable.getCellRect(idx, 0, true));
        } else {
            undoTable.clearSelection();
        }
    }
}
