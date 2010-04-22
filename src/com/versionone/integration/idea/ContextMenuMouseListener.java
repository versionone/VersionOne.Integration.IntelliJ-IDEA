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

    /**
     * Display popup menu. If menu owner is registered, acquire menu items from owner. Otherwise, just show.
     * @param e received MouseEvent
     */
    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if(menuOwner != null) {
                List<ContextMenuItemWrapper> items = menuOwner.getMenuItemsAt(e.getX(), e.getY());
                menu.removeAll();
                for(ContextMenuItemWrapper item : items) {
                    if(item.isSeparator()) {
                        menu.addSeparator();
                    } else {
                        menu.add(item.getMenuItem());
                    }
                }
            }
            
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}

interface IContextMenuOwner {
    @NotNull
    List<ContextMenuItemWrapper> getMenuItemsAt(int x, int y);
}