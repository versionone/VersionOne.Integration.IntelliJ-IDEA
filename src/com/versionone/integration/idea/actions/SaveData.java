/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.V1PluginException;

import javax.swing.*;

/**
 *
 */
public class SaveData extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        System.out.println("Save.actionPerformed()");

        final DataContext dataContext = e.getDataContext();
//        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        final Project ideaProject = DataKeys.PROJECT.getData(dataContext);

        if (ideaProject == null) {
            return;
        }
        final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
        final IDataLayer data = tc.getDataLayer();
        final ProgressManager progressManager = ProgressManager.getInstance();
        final Object[] isError = {false, "", false};

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                progressManager.getProgressIndicator().setText("Save task's data");
                try {
                    data.commitChangedTaskData();
                } catch (V1PluginException e1) {
                    isError[0] = true;
                    isError[1] = e1.getMessage();
                    isError[2] = e1.isError();
                } catch (Exception e1) {
                    isError[0] = true;
                    isError[1] = "Error connection to the VesionOne";
                    isError[2] = true;
                }
            }
        },
                "Save task's data",
                false,
                ideaProject
        );

        if ((Boolean)isError[0]) {
            Icon icon = (Boolean)isError[2] ? Messages.getErrorIcon() : Messages.getWarningIcon();
            Messages.showMessageDialog(isError[1].toString(), "Error", icon);
            return;
        }

        ActionManager.getInstance().getAction("V1.toolRefresh").actionPerformed(e);
    }
}
