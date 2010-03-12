/*(c) Copyright 2010, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.Workitem;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Render for workitems in tree. 
 */
public class WorkItemTreeTableCellRenderer extends DefaultTreeCellRenderer {
    public WorkItemTreeTableCellRenderer() {
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus) {
        Object newValue = ((Workitem)value).getProperty("Name");
        return super.getTreeCellRendererComponent(tree, newValue, sel, expanded, leaf, row, hasFocus);
    }
}
