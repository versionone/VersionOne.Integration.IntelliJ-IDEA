/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.util.ui.treetable.TreeTable;
import com.intellij.util.ui.treetable.TreeTableModel;
import com.intellij.util.ui.treetable.TreeTableCellEditor;
import com.intellij.util.ui.treetable.TreeTableTree;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import java.util.EventObject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

public class TasksTable extends TreeTable {
    private WorkItemTreeTableCellRenderer treeCellRenderer = new WorkItemTreeTableCellRenderer();

    public TasksTable(TreeTableModel treeTableModel) {
        super(treeTableModel);
        getTree().setCellRenderer(treeCellRenderer);

        //getTree().setCellEditor(new TreeCellEditor2());
        //setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor2(createTableRenderer(treeTableModel)));
    }
}
