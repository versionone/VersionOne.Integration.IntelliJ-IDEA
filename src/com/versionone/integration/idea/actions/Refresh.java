/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.TasksComponent;

import java.net.ConnectException;

public class Refresh extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        System.out.println("Refresh.actionPerformed()");//TODO delete trace output

        final DataContext dataContext = e.getDataContext();
//        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        final Project ideaProject = DataKeys.PROJECT.getData(dataContext);
        final DataLayer data = DataLayer.getInstance();
        try {
            if (ideaProject == null) {
                data.refresh();
                return;
            }
        } catch (ConnectException e1) {
            Messages.showErrorDialog(
                    "Error connection to the VesionOne server",
                    "Error");
            return;
        }

        final ProgressManager progressManager = ProgressManager.getInstance();
        final boolean[] isErrors = {false};

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                progressManager.getProgressIndicator().setText("Update tasks list");
                try {
                    data.refresh();
                } catch (ConnectException e1) {
                    isErrors[0] = true;
                }
            }
        },
                "Update tasks list",
                false,
                ideaProject
        );

        if (isErrors[0]) {
            Messages.showErrorDialog(
                    "Error connection to the VesionOne server",
                    "Error");
        }

        final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
        tc.revalidate();
    }
}
