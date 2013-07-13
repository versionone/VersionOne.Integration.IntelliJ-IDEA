package com.versionone.integration.idea;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Mouse listener that displays supplied context menu
 */
public class ContextMenuMouseListener extends MouseAdapter {
    private JPopupMenu menu;
    private IContextMenuOwner menuOwner;

    public ContextMenuMouseListener(IContextMenuOwner menuOwner) {
        this((JPopupMenu) null);
        this.menuOwner = menuOwner;
    }

    public ContextMenuMouseListener(JPopupMenu menu) {
        this.menu = menu;
    }

    public void mousePressed(MouseEvent e) {
        showPopupMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
        showPopupMenu(e);
    }

    /**
     * Display popup menu. If menu owner is registered, acquire menu items from owner. Otherwise, just show.
     * @param e received MouseEvent
     */
    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu menuToDisplay = menu;
            
            if(menuOwner != null) {
                menuToDisplay = menuOwner.getPopupMenu();
            }
            menuToDisplay.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}

interface IContextMenuOwner {
    @NotNull
    JPopupMenu getPopupMenu();
}