/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.EntityType;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.SecondaryWorkitem;

/**
 * Create in-memory Task. New entity will be persisted when user triggers Save action.
 */
public class AddTaskAction extends AbstractAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project ideaProject = resolveProject(e);
        if (ideaProject != null) {
            TasksComponent tc = resolveTasksComponent(ideaProject);

            Workitem currentItem = (Workitem)tc.getCurrentItem();
            if (currentItem != null) {
                SecondaryWorkitem newItem = createTask(currentItem);

                tc.refresh();
                tc.update();
                tc.selectNode(newItem);
            }
        }
    }

    private SecondaryWorkitem createTask(Workitem currentItem) {
        PrimaryWorkitem parent;
        if (currentItem.getType().isPrimary()) {
            parent = (PrimaryWorkitem)currentItem;
        } else {
            parent = ((SecondaryWorkitem)currentItem).parent;
        }
        SecondaryWorkitem newItem = null;
        try {
            newItem = dataLayer.createNewSecondaryWorkitem(EntityType.Task, parent);
        } catch (DataLayerException ignored) {}

        return newItem;
    }

    @Override
    public void update(AnActionEvent event) {        
        Presentation presentation = event.getPresentation();
        presentation.setEnabled(ApiDataLayer.getInstance().isConnected() && !isActionDisabled(event));
    }

    private boolean isActionDisabled(AnActionEvent event) {
        Project ideaProject = resolveProject(event);
        if (ideaProject != null) {
            TasksComponent tc = resolveTasksComponent(ideaProject);
            return tc.getCurrentItem() == null;
        }

        return true;
    }
}