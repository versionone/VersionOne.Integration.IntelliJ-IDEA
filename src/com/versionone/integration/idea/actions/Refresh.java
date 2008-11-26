/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.integration.idea.DetailsComponent;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.V1PluginException;

public class Refresh extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        System.out.println("Refresh.actionPerformed()");//TODO delete trace output

        final DataContext dataContext = e.getDataContext();
        final Project ideaProject = DataKeys.PROJECT.getData(dataContext);

        if (ideaProject == null) {
            return;
        }
        final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
        final IDataLayer data = tc.getDataLayer();
        final ProgressManager progressManager = ProgressManager.getInstance();
        final Object[] isErrors = {false, ""};

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                progressManager.getProgressIndicator().setText("Update tasks list");
                try {
                    data.refresh();
                } catch (V1PluginException e1) {
                    isErrors[0] = true;
                    isErrors[1] = e1.getMessage();
                }
            }
        },
                "Update tasks list",
                false,
                ideaProject
        );

        if ((Boolean) isErrors[0]) {
            Messages.showWarningDialog(
                    isErrors[1].toString(),
                    "Error");
        }

        tc.update();
        ideaProject.getComponent(DetailsComponent.class).update();
    }
}
