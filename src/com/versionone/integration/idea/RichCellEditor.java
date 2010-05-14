package com.versionone.integration.idea;

import com.versionone.common.sdk.Workitem;
import com.versionone.integration.idea.editors.RichDialogEditor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;

import org.jetbrains.annotations.NotNull;

/**
 * Edit html data
 */
public class RichCellEditor extends DialogCellEditor {
    private final Workitem currentItem;
    private static String action = "edit";
    private final JFrame parent;
    private final JTable table;

    public RichCellEditor(@NotNull Workitem item, @NotNull JTable table) {
        super(action);
        currentItem = item;
        this.table = table;

        parent = (JFrame) SwingUtilities.getRoot(table);
    }

    public Object getCellEditorValue() {
        return currentItem.getProperty(Workitem.DESCRIPTION_PROPERTY) == null ? "" : currentItem.getProperty(Workitem.DESCRIPTION_PROPERTY);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(action)) {
            RichDialogEditor editor = new RichDialogEditor(parent, table, "Edit description", currentItem);
            editor.setVisible(true);
        }
    }
}
