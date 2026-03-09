package de.jare.tree.ui;

import de.jare.tree.control.commands.WoodCommand;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class UndoTableModel extends AbstractTableModel {

    public static class Entry {

        public final boolean undone;      // true = auf Undo-Stack, false = auf Redo-Stack
        public final String action;       // z.B. "Edit", "Add", "Delete"
        public final String description;  // getDescription()

        public Entry(boolean undone, String action, String description) {
            this.undone = undone;
            this.action = action;
            this.description = description;
        }
    }

    private final List<Entry> entries = new ArrayList<>();
    private int selectedIndex = -1; // oberste Undo-Position (unterster undone-Eintrag)

    private static final String[] COLS = {"Status", "Action", "Description"};

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return COLS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Entry e = entries.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                e.undone ? "Undo" : "Redo";
            case 1 ->
                e.action;
            case 2 ->
                e.description;
            default ->
                "";
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void clear() {
        entries.clear();
        selectedIndex = -1;
        fireTableDataChanged();
    }

    public void addCommand(WoodCommand cmd) {
        // neuer Befehl -> alles „darüber“ (Redo-Bereich) verwerfen
        while (!entries.isEmpty() && selectedIndex < entries.size() - 1) {
            entries.remove(entries.size() - 1);
        }
        entries.add(new Entry(true, cmd.getCommandText(), cmd.getDescription()));
        selectedIndex = entries.size() - 1;
        fireTableDataChanged();
    }

    public void markUndo() {
        if (selectedIndex >= 0) {
            Entry e = entries.get(selectedIndex);
            entries.set(selectedIndex,
                    new Entry(false, e.action, e.description));
            selectedIndex--;
            fireTableRowsUpdated(Math.max(selectedIndex, 0), entries.size() - 1);
        }
    }

    public void markRedo() {
        if (selectedIndex + 1 < entries.size()) {
            selectedIndex++;
            Entry e = entries.get(selectedIndex);
            entries.set(selectedIndex,
                    new Entry(true, e.action, e.description));
            fireTableRowsUpdated(selectedIndex, entries.size() - 1);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
