/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.integration.idea.DetailsComponent;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.V1PluginException;

import javax.swing.*;

import org.apache.log4j.Logger;

public class Refresh extends AnAction {

    private static final Logger LOG = Logger.getLogger(Refresh.class);

    private Project project;

    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        Project ideaProject = PlatformDataKeys.PROJECT.getData(dataContext);

        if (ideaProject == null && project == null) {
            return;
        } else if (ideaProject == null) {
            ideaProject = project;
        }
        final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
        final IDataLayer dataLayer = tc.getDataLayer();
        
        if (dataLayer.hasChanges()) {
            int confirmRespond = Messages.showDialog("You have pending changes that will be overwritten.\nDo you want to continue?", "Refresh Warning", new String[]{"Yes", "No"}, 1, Messages.getQuestionIcon());
            if (confirmRespond == 1) {
                return;
            }
        }

        final DetailsComponent dc = ideaProject.getComponent(DetailsComponent.class);
        final ProgressManager progressManager = ProgressManager.getInstance();
        refreshData(ideaProject, tc, dataLayer, dc, progressManager);
    }

    static void refreshData(Project ideaProject, TasksComponent tc, final IDataLayer dataLayer, DetailsComponent dc, final ProgressManager progressManager) {
        final Exception[] isError = {null};
        tc.removeEdition();
        dc.removeEdition();

        ProgressManager.getInstance().runProcessWithProgressSynchronously(
                new Runnable() {
                    public void run() {
                        progressManager.getProgressIndicator().setText("Updating VersionOne Task List");
                        try {
                            dataLayer.reconnect();
                        } catch (Exception ex) {
                            LOG.error("Failed to refresh workitems.", ex);
                            isError[0] = ex;
                        }
                    }
                },
                "VersionOne Workitem List Refreshing",
                false,
                ideaProject
        );

        if (isError[0] != null) {
            Icon icon = Messages.getErrorIcon();
            Messages.showMessageDialog(isError[0].getMessage(), "Error", icon);
            return;
        }

        tc.refresh();
        tc.update();
        dc.setItem(tc.getItem());
        dc.update();
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
