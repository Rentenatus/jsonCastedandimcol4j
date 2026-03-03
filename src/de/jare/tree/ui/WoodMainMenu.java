/*
 *
 */
package de.jare.tree.ui;

import de.jare.tree.control.MasterControl;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class WoodMainMenu extends JMenuBar {

    private final JFrame frame;
    private final MasterControl master;

    public WoodMainMenu(JFrame mainFrame, MasterControl master) {
        this.frame = mainFrame;
        this.master = master;

        // Projekt-Menü
        JMenu projectMenu = new JMenu("Projekt");
        projectMenu.setMnemonic(KeyEvent.VK_P);

        JMenuItem newItem = new JMenuItem("Neu");
        JMenuItem openItem = new JMenuItem("Öffnen...");
        JMenuItem saveItem = new JMenuItem("Speichern");
        JMenuItem saveAsItem = new JMenuItem("Speichern unter...");
        JMenuItem exitItem = new JMenuItem("Beenden");

        exitItem.addActionListener(e -> frame.dispose());

        projectMenu.add(newItem);
        projectMenu.add(openItem);
        projectMenu.add(saveItem);
        projectMenu.add(saveAsItem);
        projectMenu.addSeparator();
        projectMenu.add(exitItem);

        // Edit-Menü
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem addNodeItem = new JMenuItem("Node hinzufügen");
        JMenuItem deleteNodeItem = new JMenuItem("Node löschen");
        JMenuItem renameNodeItem = new JMenuItem("Node umbenennen");

        addNodeItem.addActionListener(e -> master.fireCommand("edit.addNode"));
        deleteNodeItem.addActionListener(e -> master.fireCommand("edit.deleteNode"));
        renameNodeItem.addActionListener(e -> master.fireCommand("edit.renameNode"));

        editMenu.add(addNodeItem);
        editMenu.add(deleteNodeItem);
        editMenu.add(renameNodeItem);

        // Info-Menü
        JMenu infoMenu = new JMenu("Info");
        infoMenu.setMnemonic(KeyEvent.VK_I);

        JMenuItem aboutItem = new JMenuItem("Über...");
        aboutItem.addActionListener(e
                -> JOptionPane.showMessageDialog(
                        frame,
                        "Tree Editor\n© 2026",
                        "Über",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );
        infoMenu.add(aboutItem);

        add(projectMenu);
        add(editMenu);
        add(infoMenu);
    }
}
