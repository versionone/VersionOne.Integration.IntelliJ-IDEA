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

/**
 * Add test to the VersionOne.
 */
public class AddTestAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
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
}
