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

import javax.swing.*;

import org.apache.log4j.Logger;

public class Refresh extends AnAction {

    private static final Logger LOG = Logger.getLogger(Refresh.class);

    private Project project;

    public void actionPerformed(AnActionEvent e) {
        System.out.println("Refresh.actionPerformed()");//TODO delete trace output
        final DataContext dataContext = e.getDataContext();
        Project ideaProject = DataKeys.PROJECT.getData(dataContext);

        if (ideaProject == null && project == null) {
            return;
        } else if (ideaProject == null) {
            ideaProject = project;
        }
        final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
        final IDataLayer data = tc.getDataLayer();
        
        if (data.isTaskChanged()) {
            int confirmRespond = Messages.showDialog("Do you want to make refresh? All changed information will be reseted.", "Refresh", new String[]{"Yes", "No"}, 1, Messages.getQuestionIcon());
            if (confirmRespond == 1) {
                return;
            }
        }

        final DetailsComponent dc = ideaProject.getComponent(DetailsComponent.class);
        final ProgressManager progressManager = ProgressManager.getInstance();
        refreshData(ideaProject, tc, data, dc, progressManager);
    }

    static void refreshData(Project ideaProject, TasksComponent tc, final IDataLayer data, DetailsComponent dc, final ProgressManager progressManager) {
        final Object[] isError = {false, "", false};
        tc.removeEdition();
        dc.removeEdition();

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                progressManager.getProgressIndicator().setText("Update tasks list");
                try {
                    data.refresh();
                } catch (V1PluginException e1) {
                    LOG.warn(e1.getMessage(),e1);
                    isError[0] = true;
                    isError[1] = e1.getMessage();
                    isError[2] = e1.isError();
                }
            }
        },
                "Update tasks list",
                false,
                ideaProject
        );

        if ((Boolean) isError[0]) {
            Icon icon = (Boolean)isError[2] ? Messages.getErrorIcon() : Messages.getWarningIcon();
            Messages.showMessageDialog(isError[1].toString(), "Error", icon);
        }

        tc.update();
        dc.update();
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
