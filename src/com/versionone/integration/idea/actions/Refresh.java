/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.Messages;
import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.apiclient.V1Exception;

import java.net.ConnectException;

public class Refresh extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        System.out.println("Refresh.actionPerformed()");//TODO delete trace output

        final DataContext dataContext = e.getDataContext();
//        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        final Project ideaProject = DataKeys.PROJECT.getData(dataContext);
        final DataLayer data;
        try {
            data = DataLayer.getInstance();
            if (ideaProject == null) {
                data.refresh();
                return;
            }
        } catch (ConnectException e1) {
            Messages.showMessageDialog(
                    "Error connection to the VesionOne server",
                    "Error",
                    Messages.getErrorIcon());
            return;
        }

        final ProgressManager progressManager = ProgressManager.getInstance();
        final boolean[] a = {true};

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                progressManager.getProgressIndicator().setText("Update tasks list");
                try {
                    data.refresh();
                } catch (ConnectException e1) {
                    a[0] = false;
                }
            }},
            "Update tasks list",
            false,
            ideaProject
        );

        if (!a[0]) {
            Messages.showMessageDialog(
                    "Error connection to the VesionOne server",
                    "Error",
                    Messages.getErrorIcon());            
        }

        final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
        tc.revalidate();
    }
}
