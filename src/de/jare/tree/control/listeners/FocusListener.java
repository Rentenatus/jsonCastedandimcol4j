package de.jare.tree.control.listeners;

// Reagiert auf Fokuswechsel (Editor wurde aktiv/inaktiv)
public interface FocusListener {

    void onFocusGained();

    void onFocusLost();
}
