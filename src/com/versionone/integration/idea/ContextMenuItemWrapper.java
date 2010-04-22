package com.versionone.integration.idea;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class ContextMenuItemWrapper {
    private final JMenuItem menuItem;
    private final boolean isSeparator;

    private ContextMenuItemWrapper(JMenuItem menuItem) {
        this.menuItem = menuItem;
        isSeparator = menuItem == null;
    }

    public JMenuItem getMenuItem() {
        return menuItem;
    }

    public static ContextMenuItemWrapper createSeparator() {
        return new ContextMenuItemWrapper(null);
    }

    public static ContextMenuItemWrapper createFromMenuItem(@NotNull JMenuItem menuItem) {
        return new ContextMenuItemWrapper(menuItem);
    }

    public boolean isSeparator() {
        return isSeparator;
    }
}
