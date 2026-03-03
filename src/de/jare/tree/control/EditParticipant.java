package de.jare.tree.control;

public interface EditParticipant {

    /**
     * Wird aufgerufen, wenn dieser Editor Fokus bekommt.
     */
    void onFocusGained();

    /**
     * Wird aufgerufen, wenn der Fokus auf einen anderen Editor wechselt.
     */
    void onFocusLost();

    /**
     * Men�-/Command-Ereignisse (Add, Delete, Rename, etc.).
     * @param commandId
     */
    void onCommand(String commandId);
}
