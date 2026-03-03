package de.jare.tree.control;

import de.jare.tree.control.listeners.ContentListener;
import de.jare.tree.control.listeners.FocusListener;
import de.jare.tree.control.listeners.SelectionListener;

public class MasterControl {

    // Channels
    private final Orator<FocusListener> focusOrator = new Orator<>();
    private final Orator<SelectionListener> selectionOrator = new Orator<>();
    private final Orator<ContentListener> contentOrator = new Orator<>();

    // welcher Editor ist aktuell aktiv (Tab-basiert)?
    private Object activeEditor; // bewusst generisch

    // Registrierung
    public void addFocusListener(FocusListener l) {
        focusOrator.addListener(l);
    }

    public void addSelectionListener(SelectionListener l) {
        selectionOrator.addListener(l);
    }

    public void addContentListener(ContentListener l) {
        contentOrator.addListener(l);
    }

    public void removeFocusListener(FocusListener l) {
        focusOrator.removeListener(l);
    }

    public void removeSelectionListener(SelectionListener l) {
        selectionOrator.removeListener(l);
    }

    public void removeContentListener(ContentListener l) {
        contentOrator.removeListener(l);
    }

    // Vom UI (z.B. JTabbedPane) gerufen, wenn ein Tab gew�hlt wird
    public void setActiveEditor(Object editor) {
        Object previous = this.activeEditor;
        if (previous == editor) {
            return;
        }
        this.activeEditor = editor;

        // Fokus-Events verteilen
        if (previous != null) {
            focusOrator.say(l -> l.onFocusLost());
        }
        if (editor != null) {
            focusOrator.say(l -> l.onFocusGained());
        }
    }

    // Vom aktiven Editor gerufen, wenn sich die Node-Selektion �ndert
    public void fireSelection(Object node) {
        selectionOrator.say(l -> l.onNodeSelected(node));
    }

    // Vom Men� / KI / Toolbar gerufen
    public void fireCommand(String commandId) {
        contentOrator.say(l -> l.onCommand(commandId));
    }

    public Object getActiveEditor() {
        return activeEditor;
    }
}
