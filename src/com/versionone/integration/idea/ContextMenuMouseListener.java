package com.versionone.integration.idea;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Mouse listener that displays supplied context menu
 */
public class ContextMenuMouseListener extends MouseAdapter {
    private JPopupMenu menu;

    public ContextMenuMouseListener(final JPopupMenu menu) {
        this.menu = menu;
    }

    public void mousePressed(MouseEvent e) {
        showPopupMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
        showPopupMenu(e);
    }

    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}