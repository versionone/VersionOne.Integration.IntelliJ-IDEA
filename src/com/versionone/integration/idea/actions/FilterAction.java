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
import com.versionone.integration.idea.DetailsComponent;
import org.apache.log4j.Logger;

import javax.swing.*;

public class FilterAction extends AnAction {

    private static final Logger LOG = Logger.getLogger(FilterAction.class);
    private WorkspaceSettings settings;
    private Project project;

    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
//        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        Project ideaProject = DataKeys.PROJECT.getData(dataContext);
        if (ideaProject == null && project != null) {
            ideaProject = project;
        }

        if (ideaProject != null) {
            filterDialog(ideaProject);
            //ActionManager.getInstance().getAction("V1.toolRefresh").actionPerformed(e);
        }
    }

    public void filterDialog(final Project ideaProject) {

        final ProgressManager progressManager = ProgressManager.getInstance();
        final ProjectTreeNode[] projectsRoot = new ProjectTreeNode[1];
        final Object[] isError = {false, "", false};
        final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
        final DetailsComponent dc = ideaProject.getComponent(DetailsComponent.class);
        final IDataLayer data = tc.getDataLayer();

        if (data.isTaskChanged()) {
            int confirmRespond = Messages.showDialog("You have pending changes that will be overwritten if you change projects.\nDo you wish to continue?.", "Filter Warning", new String[]{"Yes", "No"}, 1, Messages.getQuestionIcon());
            if (confirmRespond == 1) {
                return;
            }
        }

        tc.removeEdition();
        dc.removeEdition();

        boolean isCanceled = ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                final ProgressIndicator indicator = progressManager.getProgressIndicator();
//                data.setProgressIndicator(indicator);
                indicator.setText("Loading VersionOne Projects");
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
                "Loading VersionOne Projects",
                true,
                ideaProject
        );

        if (!isCanceled) {
            return;
        }

        if ((Boolean)isError[0]) {
            Icon icon = (Boolean)isError[2] ? Messages.getErrorIcon() : Messages.getWarningIcon();
            Messages.showMessageDialog(isError[1].toString(), "Error", icon);
            return;
        }

        final FilterForm form = new FilterForm(projectsRoot[0], settings);
        if (ShowSettingsUtil.getInstance().editConfigurable(ideaProject, form)) {
            Refresh.refreshData(ideaProject, tc, data, dc, progressManager);
        }
    }

    public void setSettings(WorkspaceSettings settings) {
        this.settings = settings;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
