package de.jare.tree.ui;

import de.jare.tree.control.listeners.SelectionListener;
import javax.swing.table.AbstractTableModel;

public class PropertyTableModel extends AbstractTableModel implements SelectionListener {

    private final String[] columnNames = {"Name", "Value", "Typ"};
    private Object[][] data = {
        {"x", 0, "int"},
        {"y", 0, "int"},
        {"z", 0, "int"}
    };

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length; // keine Header anzeigen, aber intern genutzt
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        data[row][col] = aValue;
        fireTableCellUpdated(row, col);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Spalte 0 = Name (fix), Spalte 1 = Value (editierbar), Spalte 2 = Typ (optional editierbar)
        return columnIndex == 1;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column]; // JTable-Header blenden wir gleich aus
    }

    @Override
    public void onNodeSelected(Object node) {
        updateProperties(node);
    }

    @Override
    public void onEditorSelected(Object editor) {
        // NoOp
    }

    private void updateProperties(Object node) {
        if (node == null) {

            setProperties(new Object[0][0]);

            return;
        }

        Object[][] data = {
            {"x", node.toString(), "String"},
            {"y", 0, "int"},
            {"z", 0, "int"}
        };
        setProperties(data);
    }

    // Zum sp�teren Anpassen der Daten (z.B. je nach Node)
    public void setProperties(Object[][] newData) {
        this.data = newData;
        fireTableDataChanged();
    }

}
