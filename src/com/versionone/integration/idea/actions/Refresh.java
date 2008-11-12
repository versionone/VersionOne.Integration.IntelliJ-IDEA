/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.project.Project;
import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.TasksComponent;

public class Refresh extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        System.out.println("Refresh.actionPerformed()");//TODO delete trace output
        DataLayer.getInstance().refresh();
        final DataContext dataContext = e.getDataContext();
        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        if (ideaProject == null) {
            return;
        }
        final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
        tc.revalidate();
    }
}
