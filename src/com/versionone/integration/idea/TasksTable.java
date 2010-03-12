/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.util.ui.treetable.TreeTable;
import com.intellij.util.ui.treetable.TreeTableModel;

public class TasksTable extends TreeTable {

    public TasksTable(TreeTableModel treeTableModel) {
        super(treeTableModel);

        getTree().setCellRenderer(new WorkItemTreeTableCellRenderer());
    }

}
