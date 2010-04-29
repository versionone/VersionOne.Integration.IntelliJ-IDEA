package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.DetailsComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class AbstractAction extends AnAction {

    protected IDataLayer dataLayer;

    protected void displayError(String message) {
        Icon icon = Messages.getErrorIcon();
        Messages.showMessageDialog(message, "Error", icon);
    }

    public void setDataLayer(@NotNull IDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    protected TasksComponent resolveTasksComponent(@NotNull AnActionEvent e) {
        Project ideaProject = resolveProject(e);
        return resolveTasksComponent(ideaProject);
    }

    protected DetailsComponent resolveDetailsComponent(@NotNull AnActionEvent e) {
        Project ideaProject = resolveProject(e);
        return resolveDetailsComponent(ideaProject);
    }

    protected TasksComponent resolveTasksComponent(Project ideaProject) {
        if (ideaProject != null) {
            return ideaProject.getComponent(TasksComponent.class);
        }
        return null;
    }

    protected DetailsComponent resolveDetailsComponent(Project ideaProject) {
        if (ideaProject != null) {
            return ideaProject.getComponent(DetailsComponent.class);
        }
        return null;
    }

    protected Project resolveProject(@NotNull AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        return PlatformDataKeys.PROJECT.getData(dataContext);
    }
}
