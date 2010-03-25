/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.util.ui.treetable.TreeTable;
import com.intellij.util.ui.treetable.TreeTableModel;

public class TasksTable extends TreeTable {
    private WorkItemTreeTableCellRenderer treeCellRenderer = new WorkItemTreeTableCellRenderer();

    public TasksTable(TreeTableModel treeTableModel) {
        super(treeTableModel);
        getTree().setCellRenderer(treeCellRenderer);

        //getTree().setCellEditor(new TreeCellEditor2());
        //setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor2(createTableRenderer(treeTableModel)));
    }
}
