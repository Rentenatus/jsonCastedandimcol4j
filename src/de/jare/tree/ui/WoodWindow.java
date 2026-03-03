/*
 *
 */
package de.jare.tree.ui;

import de.jare.tree.control.MasterControl;
import java.awt.*;
import javax.swing.*;

public class WoodWindow extends JFrame {

    private final MasterControl master;
    private final JTabbedPane centerTabs;
    private final WoodEditTree editorTree1;
    private final WoodEditTree editorTree2;

    public WoodWindow() {
        master = new MasterControl();

        setTitle("Tree Editor");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        WoodMainMenu bar = new WoodMainMenu(this, master);
        setJMenuBar(bar);

        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        WoodProjektTree projectTree = new WoodProjektTree("Project", "Node1", "Node2", "Folder");
        projectTree.setPreferredSize(new Dimension(250, 0));
        horizontalSplit.setLeftComponent(new JScrollPane(projectTree));

        centerTabs = new JTabbedPane();

        editorTree1 = new WoodEditTree(master, "Root1", "Scene1", "Character1");
        editorTree2 = new WoodEditTree(master, "Root2", "Scene2", "Character2");

        centerTabs.addTab("Tree Editor 1", new JScrollPane(editorTree1));
        centerTabs.addTab("Tree Editor 2", new JScrollPane(editorTree2));
        horizontalSplit.setRightComponent(centerTabs);

        // Tab-Wechsel steuert aktiven Editor
        centerTabs.addChangeListener(e -> {
            int idx = centerTabs.getSelectedIndex();
            Object editor = switch (idx) {
                case 0 ->
                    editorTree1;
                case 1 ->
                    editorTree2;
                default ->
                    null;
            };
            master.setActiveEditor(editor);
        });
        // initial
        master.setActiveEditor(editorTree1);

        WoodEditPopup popup = new WoodEditPopup(master);
        WoodEditPopup.installOn(editorTree1, popup);
        WoodEditPopup.installOn(editorTree2, popup);

        JTabbedPane bottomTabs = new JTabbedPane();
        bottomTabs.addTab("Properties", createPropertiesPanel());
        bottomTabs.addTab("KI Assistant", createKIAssistant());
        verticalSplit.setTopComponent(horizontalSplit);
        verticalSplit.setBottomComponent(bottomTabs);

        horizontalSplit.setDividerLocation(300);
        verticalSplit.setDividerLocation(600);

        add(verticalSplit, BorderLayout.CENTER);

        // Properties an Selection-Orator h�ngen
        master.addSelectionListener(propertyModel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JTable propertyTable;
    private PropertyTableModel propertyModel;

    private JPanel createPropertiesPanel() {
        propertyModel = new PropertyTableModel();
        propertyTable = new JTable(propertyModel);
        propertyTable.setFillsViewportHeight(true);
        propertyTable.getTableHeader().setVisible(false); // keine �berschrift anzeigen

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(propertyTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createKIAssistant() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea prompt = new JTextArea(5, 20);
        prompt.setText("KI-Prompt hier...");
        JButton askBtn = new JButton("KI fragen");
        panel.add(new JScrollPane(prompt), BorderLayout.CENTER);
        panel.add(askBtn, BorderLayout.SOUTH);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WoodWindow::new);
    }

}
