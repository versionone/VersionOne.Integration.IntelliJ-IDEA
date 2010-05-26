/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.versionone.integration.idea.WorkspaceSettings;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.DetailsComponent;
import com.versionone.common.sdk.IDataLayer;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Filter to show only workitems which was assigned to current user or show all workitems
 * enable - show only current user workitems
 * disable - show all workitems
 */
public class ShowAllItemFilterAction extends ToggleAction {
    private WorkspaceSettings settings;
    private IDataLayer dataLayer;

    @Override
    public boolean isSelected(AnActionEvent e) {
        return !settings.isShowAllTask;
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        settings.isShowAllTask = !state;
        dataLayer.setShowAllTasks(!state);
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

    public void setDataLayer(@NotNull IDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(dataLayer.isConnected() && settings.isEnabled);
    }
}
