/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.SecondaryWorkitem;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.EntityType;
import com.versionone.common.sdk.DataLayerException;

/**
 * Create in-memory Test. New entity will be persisted when user triggers Save action.
 */
public class AddTestAction extends AbstractAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project ideaProject = resolveProject(e);
        if (ideaProject != null) {
            TasksComponent tc = resolveTasksComponent(ideaProject);

            Workitem currentItem = (Workitem)tc.getCurrentItem();
            if (currentItem != null) {
                SecondaryWorkitem newItem = createTest(currentItem);

                tc.refresh();
                tc.update();
                tc.selectNode(newItem);
            }
        }
    }

    private SecondaryWorkitem createTest(Workitem currentItem) {
        PrimaryWorkitem parent;
        if (currentItem.getType().isPrimary()) {
            parent = (PrimaryWorkitem)currentItem;
        } else {
            parent = ((SecondaryWorkitem)currentItem).parent;
        }
        SecondaryWorkitem newItem = null;
        try {
            newItem = dataLayer.createNewSecondaryWorkitem(EntityType.Test, parent);
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
            TasksComponent tc = resolveTasksComponent(event);
            return tc.getCurrentItem() == null;
        }

        return true;
    }
}
