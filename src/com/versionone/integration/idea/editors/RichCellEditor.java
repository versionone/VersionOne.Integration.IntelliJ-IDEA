package com.versionone.integration.idea.editors;

import com.versionone.common.sdk.Workitem;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Edit html data
 */
public class RichCellEditor extends DialogCellEditor {
    private final Workitem currentItem;
    private static String action = "edit";
    private final JFrame parent;
    private final JTable table;
    private final String attribute;

    public RichCellEditor(@NotNull Workitem item, @NotNull String attribute, @NotNull JTable table) {
        super(action);
        currentItem = item;
        this.attribute = attribute;
        this.table = table;

        parent = (JFrame) SwingUtilities.getRoot(table);
    }

    public Object getCellEditorValue() {
        return currentItem.getProperty(Workitem.DESCRIPTION_PROPERTY) == null ? "" : currentItem.getProperty(Workitem.DESCRIPTION_PROPERTY);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(action)) {
            RichDialogEditor editor = new RichDialogEditor(parent, table, "Edit " + attribute, currentItem);
            editor.setVisible(true);
        }
    }
}
