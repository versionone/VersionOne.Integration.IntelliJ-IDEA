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
    private IContextMenuOwner menuOwner = null;

    public ContextMenuMouseListener(JPopupMenu menu, IContextMenuOwner menuOwner) {
        this(menu);
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

    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
            List<JMenuItem> items = menuOwner.getMenuItemsAt(e.getX(), e.getY());
            menu.removeAll();
            for(JMenuItem item : items) {
                menu.add(item);
            }
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}

interface IContextMenuOwner {
    @NotNull
    List<JMenuItem> getMenuItemsAt(int x, int y);
}