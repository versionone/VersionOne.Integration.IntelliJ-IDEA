/*(c) Copyright 2010, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.EntityType;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Render for workitems in tree. 
 */
public class WorkItemTreeTableCellRenderer extends DefaultTreeCellRenderer {
    private final Map<EntityType, Icon> icons;
    private final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
    private final Color defaultForeColor = getForeground();

    public WorkItemTreeTableCellRenderer() {
        icons = new HashMap<EntityType, Icon>();

        icons.put(EntityType.Defect, IconLoader.getIcon("/defect.gif"));
        icons.put(EntityType.Story, IconLoader.getIcon("/story.gif"));
        icons.put(EntityType.Test, IconLoader.getIcon("/test.gif"));
        icons.put(EntityType.Task, IconLoader.getIcon("/task.gif"));
    }

    /**
     * Sets specify icon for tree.
     * @param icon - icon for nodes.
     */
    private void setWorkitemIcon(Icon icon) {
        setIcon(icon);
        setOpenIcon(icon);
        setClosedIcon(icon);
        setLeafIcon(icon);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus) {

        Object newValue = "***ERROR***";
        Workitem item = null;

        if (value instanceof Workitem) {
            item = (Workitem) value;
            newValue = item.getProperty("Name");
            setWorkitemIcon(icons.get(((Workitem)value).getType()));
        }
        
        if(item != null) {
            if (item.hasChanges()) {
                setBackgroundNonSelectionColor(colorsScheme.getColor(ColorKey.find("V1_CHANGED_ROW")));
                setForeground(Color.black);
            } else {
                setBackgroundNonSelectionColor(getBackground());
                setForeground(defaultForeColor);
            }
        }

        return super.getTreeCellRendererComponent(tree, newValue, sel, expanded, leaf, row, hasFocus);
    }
}
