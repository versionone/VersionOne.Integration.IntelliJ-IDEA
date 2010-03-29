/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.util.ui.treetable.TreeTable;
import com.intellij.util.ui.treetable.TreeTableModel;

import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TasksTable extends TreeTable {
    private final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();

    public TasksTable(TreeTableModel treeTableModel) {
        super(treeTableModel);
        WorkItemTreeTableCellRenderer treeCellRenderer = new WorkItemTreeTableCellRenderer();
        getTree().setCellRenderer(treeCellRenderer);
        //getTree().setCellEditor(new TreeCellEditor2());
        //setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor2(createTableRenderer(treeTableModel)));
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        System.out.println("row " + rowIndex + ", col " + vColIndex + ", converted " + convertRowIndexToModel(rowIndex));
        if (rowIndex != getSelectedRow()) {
            TasksModel model = (TasksModel) getTableModel();
            Object lastPathComponent = getTree().getPathForRow(rowIndex).getLastPathComponent();
            if (model.isRowChanged(rowIndex, lastPathComponent)) {
                c.setBackground(colorsScheme.getColor(ColorKey.find("V1_CHANGED_ROW")));
                c.setForeground(Color.black);
            } else {
                c.setBackground(getBackground());
                c.setForeground(getForeground());
            }
        } else {
            c.setBackground(getSelectionBackground());
            c.setForeground(getSelectionForeground());
        }

        return c;
    }
}
