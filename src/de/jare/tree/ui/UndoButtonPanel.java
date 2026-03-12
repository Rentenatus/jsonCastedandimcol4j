package de.jare.tree.ui;

import de.jare.tree.control.MasterControl;
import de.jare.tree.control.UndoManager;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UndoButtonPanel extends JPanel {

    private final UndoManager undoMan;

    public UndoButtonPanel(MasterControl master) {
        this.undoMan = master.getUndoManager();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(4, 4, 4, 4));

        JButton btnUndo = createIconButton("/icons/undo.png", "Undo");
        JButton btnRedo = createIconButton("/icons/redo.png", "Redo");
        JButton btnSkipRedo = createIconButton("/icons/skip_redo.png", "Skip Redo");

        btnUndo.addActionListener(e -> undoMan.undo());
        btnRedo.addActionListener(e -> undoMan.redo());
        btnSkipRedo.addActionListener(e -> undoMan.skip_redo());

        add(btnUndo);
        add(Box.createVerticalStrut(4));
        add(btnRedo);
        add(Box.createVerticalStrut(8));
        add(btnSkipRedo);
    }

    private JButton createIconButton(String resource, String tooltip) {
        Icon icon = new ImageIcon(getClass().getResource(resource));
        JButton b = new JButton(icon);
        b.setToolTipText(tooltip);
        b.setFocusable(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(true);
        b.setOpaque(false);
        // kleine Standardgr��e
        b.setMaximumSize(new Dimension(32, 32));
        b.setPreferredSize(new Dimension(32, 32));
        return b;
    }
}
