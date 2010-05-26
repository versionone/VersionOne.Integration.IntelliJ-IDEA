/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.integration.idea.DetailsComponent;
import com.versionone.integration.idea.TasksComponent;

import javax.swing.*;

import org.apache.log4j.Logger;

public class RefreshAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(RefreshAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project ideaProject = resolveProject(e);

        if (ideaProject == null) {
            return;
        }
        final IDataLayer dataLayer = this.dataLayer;
        //TODO need to find way to recognize who was triggered this action
        String text = e.getActionManager().getAction("V1.toolRefresh").getTemplatePresentation().getText();
        if (dataLayer.hasChanges() && e.getPresentation().getText().equals(text)) {
            int confirmRespond = Messages.showDialog("You have pending changes that will be overwritten.\n" +
                                                     "Do you want to continue?", "Refresh Warning",
                                                     new String[]{"Yes", "No"}, 1, Messages.getQuestionIcon());
            if (confirmRespond == 1) {
                return;
            }
        }
        final TasksComponent tc = resolveTasksComponent(ideaProject);
        final DetailsComponent dc = resolveDetailsComponent(ideaProject);
        final ProgressManager progressManager = ProgressManager.getInstance();
        refreshData(ideaProject, dataLayer, tc, dc, progressManager);
    }

    static void refreshData(Project ideaProject, final IDataLayer dataLayer,
                            TasksComponent tc, DetailsComponent dc, final ProgressManager progressManager) {
        final Exception[] isError = {null};
        tc.removeEdition();
        dc.removeEdition();

        ProgressManager.getInstance().runProcessWithProgressSynchronously(
                new Runnable() {
                    public void run() {
                        progressManager.getProgressIndicator().setText("Updating VersionOne Task List");
                        try {
                            dataLayer.reconnect();
                        } catch (DataLayerException ex) {
                            LOG.warn("Failed to refresh workitems.", ex);
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
        dc.setItem(tc.getCurrentItem());
        dc.update();
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(dataLayer.isConnected() && getSettings().isEnabled);
    }
}
