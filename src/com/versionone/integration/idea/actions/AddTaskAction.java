/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.EntityType;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.SecondaryWorkitem;

/**
 * Create task to VersionOne action
 */
public class AddTaskAction extends AnAction {
    private IDataLayer dataLayer;

    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        Project ideaProject = PlatformDataKeys.PROJECT.getData(dataContext);
        if (ideaProject != null && dataLayer.isConnected()) {
            TasksComponent tc = ideaProject.getComponent(TasksComponent.class);

            Workitem currentItem = (Workitem)tc.getCurrentItem();
            if (currentItem != null) {
                createTask(currentItem, tc);
            }
        }
    }

    private void createTask(Workitem currentItem, TasksComponent tc) {
        PrimaryWorkitem parent;
        if (currentItem.getType().isPrimary()) {
            parent = (PrimaryWorkitem)currentItem;
        } else {
            parent = ((SecondaryWorkitem)currentItem).parent;
        }
        SecondaryWorkitem newItem = null;
        try {
            newItem = dataLayer.createNewSecondaryWorkitem(EntityType.Task, parent);
        } catch (DataLayerException ex) {}

        tc.refresh();
        tc.update();
        tc.selectNode(newItem);
    }

    public void update(AnActionEvent event) {        
        Presentation presentation = event.getPresentation();
        presentation.setEnabled(ApiDataLayer.getInstance().isConnected() && !isActionDisabled(event));
    }

    private boolean isActionDisabled(AnActionEvent event) {
        final DataContext dataContext = event.getDataContext();
        Project ideaProject = PlatformDataKeys.PROJECT.getData(dataContext);
        if (ideaProject != null) {
            TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
            return tc.getCurrentItem() == null;
        }

        return true;
    }

    public void setDataLayer(IDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }
}