/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.versionone.integration.idea.WorkspaceSettings;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.DetailsComponent;
import com.versionone.common.sdk.IDataLayer;

/**
 * Filter to show only workitems which was assigned to current user or show all workitems
 */
public class ShowAllItemFilterAction extends ToggleAction {
    private WorkspaceSettings settings;
    private IDataLayer dataLayer;

    public boolean isSelected(AnActionEvent e) {
        return settings.isShowAllTask;
    }

    public void setSelected(AnActionEvent e, boolean state) {
        settings.isShowAllTask = state;
        dataLayer.setShowAllTasks(state);
        final DataContext dataContext = e.getDataContext();
        Project ideaProject = PlatformDataKeys.PROJECT.getData(dataContext);
        if (ideaProject != null ) {
            final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
            final DetailsComponent dc = ideaProject.getComponent(DetailsComponent.class);
            tc.refresh();
            tc.update();
            dc.setItem(tc.getCurrentItem());
            dc.update();
        }
    }

    public void setSettings(WorkspaceSettings settings) {
        this.settings = settings;
    }

    public void setDataLayer(IDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }
}
