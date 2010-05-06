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
    private final RichDialogEditor editor;

    public RichCellEditor(@NotNull Workitem item, @NotNull JTable table) {
        super(action);
        currentItem = item;

        JFrame parent = (JFrame) SwingUtilities.getRoot(table);
        editor = new RichDialogEditor(parent, "HTML editor");
    }

    public Object getCellEditorValue() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(action)) {
            editor.setVisible(true);
        }
    }
}
