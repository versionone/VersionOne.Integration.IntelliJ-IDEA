package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.integration.idea.TasksComponent;
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

    protected TasksComponent resolveTasksComponent(AnActionEvent e) {
        Project ideaProject = resolveProject(e);

        if (ideaProject != null) {
            return ideaProject.getComponent(TasksComponent.class);
        }

        return null;
    }

    protected Project resolveProject(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        return PlatformDataKeys.PROJECT.getData(dataContext);
    }
}
