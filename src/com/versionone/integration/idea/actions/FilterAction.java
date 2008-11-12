/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.FilterForm;
import com.versionone.integration.idea.ProjectTreeNode;
import com.versionone.integration.idea.TasksComponent;
import org.apache.log4j.Logger;

public class FilterAction extends AnAction {

    private static final Logger LOG = Logger.getLogger(FilterAction.class);

    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        if (ideaProject != null && filterDialog(ideaProject)) {
            DataLayer.getInstance().refresh();
            final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
            tc.updateDisplayName();
        }
    }

    public static boolean filterDialog(Project ideaProject) {
        final ProjectTreeNode projectsRoot = DataLayer.getInstance().getProjects();
        final FilterForm form = new FilterForm(projectsRoot);
        return ShowSettingsUtil.getInstance().editConfigurable(ideaProject, form);
    }
}
