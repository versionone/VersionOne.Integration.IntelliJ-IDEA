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
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.ProjectTreeNode;
import com.versionone.integration.idea.FilterForm;
import com.versionone.integration.idea.WorkspaceSettings;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.V1PluginException;
import org.apache.log4j.Logger;

import javax.swing.*;

public class FilterAction extends AnAction {

    private static final Logger LOG = Logger.getLogger(FilterAction.class);
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
        final Object[] isError = {false, "", false};
        final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
        final IDataLayer data = tc.getDataLayer();

        boolean isCanceled = ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                final ProgressIndicator indicator = progressManager.getProgressIndicator();
//                data.setProgressIndicator(indicator);
                indicator.setText("Loading project list");
                try {
                    projectsRoot[0] = data.getProjects();
                } catch (V1PluginException e) {
                    isError[0] = true;
                    isError[1] = e.getMessage();
                    isError[2] = e.isError();
                }
//                data.setProgressIndicator(null);
            }
        },
                "Loading project list",
                true,
                ideaProject
        );

        if (!isCanceled) {
            return false;
        }

        if ((Boolean)isError[0]) {
            Icon icon = (Boolean)isError[2] ? Messages.getErrorIcon() : Messages.getWarningIcon();
            Messages.showMessageDialog(isError[1].toString(), "Error", icon);
            return false;
        }

        final FilterForm form = new FilterForm(projectsRoot[0], settings);
        return ShowSettingsUtil.getInstance().editConfigurable(ideaProject, form);
    }

    public void setSettings(WorkspaceSettings settings) {
        this.settings = settings;
    }
}
