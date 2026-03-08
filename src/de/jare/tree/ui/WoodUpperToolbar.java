package de.jare.tree.ui;

import de.jare.tree.control.MasterControl;
import de.jare.tree.control.UndoManager;
import de.jare.tree.control.listeners.ContentListener;
import de.jare.tree.control.listeners.TreeSelectionListener;
import java.awt.FlowLayout;
import javax.swing.*;

/**
 * Upper toolbar above the editor tabs with undo/redo buttons.
 */
public class WoodUpperToolbar extends JPanel implements ContentListener, TreeSelectionListener {

    private final MasterControl master;
    private final UndoManager undoMan;

    private final JButton btnUndo;
    private final JButton btnRedo;

    public WoodUpperToolbar(MasterControl master, UndoManager undoMan) {
        super(new FlowLayout(FlowLayout.LEFT));
        this.master = master;
        this.undoMan = undoMan;

        Icon undoIcon = new ImageIcon(getClass().getResource("/icons/undo.png"));
        Icon redoIcon = new ImageIcon(getClass().getResource("/icons/redo.png"));

        btnUndo = new JButton(undoIcon);
        btnUndo.setText(null);
        btnUndo.setBorderPainted(true);
        btnUndo.setContentAreaFilled(false);
        btnUndo.setFocusPainted(false);
        btnUndo.setOpaque(false);

        btnRedo = new JButton(redoIcon);
        btnRedo.setText(null);
        btnRedo.setBorderPainted(true);
        btnRedo.setContentAreaFilled(false);
        btnRedo.setFocusPainted(false);
        btnRedo.setOpaque(false);

        btnUndo.addActionListener(e -> {
            undoMan.undo();
            updateButtons();
        });
        btnRedo.addActionListener(e -> {
            undoMan.redo();
            updateButtons();
        });

        add(btnUndo);
        add(btnRedo);

        // initialer Zustand
        updateButtons();

        // im MasterControl registrieren:
        master.addContentListener(10, this);
        master.addSelectionListener(10, this);
    }

    private void updateButtons() {
        btnUndo.setEnabled(undoMan.canUndo());
        btnRedo.setEnabled(undoMan.canRedo());
    }

    @Override
    public void onCommand(String commandId, Object... payload) {
        updateButtons();
    }

    @Override
    public void onNodeSelected(Object node, Object... payload) {
        updateButtons();
    }

    @Override
    public void onEditorSelected(JTree editor, Object... payload) {
        updateButtons();
    }
}
