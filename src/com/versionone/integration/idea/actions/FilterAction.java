/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.integration.idea.FilterForm;
import com.versionone.integration.idea.WorkspaceSettings;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.DetailsComponent;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * Filter action to filter workitems by project.
 */
public class FilterAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(FilterAction.class);
    //private WorkspaceSettings settings;
    //private Project project;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project ideaProject = resolveProject(e);
        /*
        if (ideaProject == null && project != null) {
            ideaProject = project;
        }
        */
        if (ideaProject != null) {
            if (filterDialog(ideaProject)) {
                ActionManager.getInstance().getAction("V1.toolRefresh").actionPerformed(e);
            }
        }
    }

    private boolean filterDialog(final Project ideaProject) {
        final ProgressManager progressManager = ProgressManager.getInstance();
        final Object[] res = new Object[1];
        final TasksComponent tc = resolveTasksComponent(ideaProject);
        final DetailsComponent dc = resolveDetailsComponent(ideaProject);
        final IDataLayer dataLayer = ApiDataLayer.getInstance();

        if (dataLayer.hasChanges()) {
            int confirmRespond = Messages.showDialog("You have pending changes that will be overwritten if you change " +
                    "projects.\nDo you wish to continue?.", "Filter Warning",
                    new String[]{"Yes", "No"}, 1, Messages.getQuestionIcon());
            if (confirmRespond == 1) {
                return false;
            }
        }
        tc.removeEdition();
        dc.removeEdition();

        boolean isCanceled = progressManager.runProcessWithProgressSynchronously(
                new Runnable() {
                    public void run() {
                        try {
                            res[0] = dataLayer.getProjectTree();
                        } catch (Exception ex) {
                            res[0] = ex;
                        }
                    }
                },
                "Loading VersionOne Projects", true, ideaProject
        );
        if (!isCanceled) {
            return false;
        }
        if (res[0] instanceof Exception) {
            final Exception ex = (Exception) res[0];
            LOG.warn("Failed to get list of projects.", ex);
            Messages.showMessageDialog(ideaProject, ex.getMessage(), "Error", Messages.getErrorIcon());
            return false;
        }
        List<com.versionone.common.sdk.Project> projectsRoot = (List<com.versionone.common.sdk.Project>) res[0];
        final FilterForm form = new FilterForm(projectsRoot, settings, dataLayer);
        return ShowSettingsUtil.getInstance().editConfigurable(ideaProject, form);
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(getSettings().isEnable);
    }

    /*
    public void setProject(Project project) {
        this.project = project;
    }
    */
}
