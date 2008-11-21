/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.FilterForm;
import com.versionone.integration.idea.ProjectTreeNode;
import com.versionone.integration.idea.WorkspaceSettings;
import org.apache.log4j.Logger;

import java.net.ConnectException;

public class FilterAction extends AnAction {

    private static final Logger LOG = Logger.getLogger(FilterAction.class);
    private DataLayer dataLayer;
    private WorkspaceSettings settings;

    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
//        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        final Project ideaProject = DataKeys.PROJECT.getData(dataContext);
        if (ideaProject != null && filterDialog(ideaProject)) {
            ActionManager.getInstance().getAction("V1.toolRefresh").actionPerformed(e);
        }
    }

    public boolean filterDialog(final Project ideaProject) {

        final ProgressManager progressManager = ProgressManager.getInstance();
        final ProjectTreeNode[] projectsRoot = new ProjectTreeNode[1];
        final boolean[] isError = new boolean[]{false};

        final DataLayer data = dataLayer;

        boolean isCanceled = ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                final ProgressIndicator indicator = progressManager.getProgressIndicator();
                data.setProgressIndicator(indicator);
                indicator.setText("Loading project list");
                try {
                    projectsRoot[0] = data.getProjects();
                } catch (ConnectException e) {
                    isError[0] = true;
                }
                data.setProgressIndicator(null);
            }
        },
                "Loading project list",
                true,
                ideaProject
        );

        if (!isCanceled) {
            return false;
        }

        if (isError[0]) {
            Messages.showErrorDialog(
                    "Error connection to the VesionOne server",
                    "Error");
            return false;
        }

        final FilterForm form = new FilterForm(projectsRoot[0], settings);
        return ShowSettingsUtil.getInstance().editConfigurable(ideaProject, form);
    }

    public void setDataLayer(DataLayer data) {
        this.dataLayer = data;
    }

    public void setSettings(WorkspaceSettings settings) {
        this.settings = settings;
    }
}
