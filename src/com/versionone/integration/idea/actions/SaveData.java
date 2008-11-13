/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.versionone.integration.idea.DataLayer;

/**
 *
 */
public class SaveData extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        System.out.println("Save.actionPerformed()");

        final DataContext dataContext = e.getDataContext();
        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        final ProgressManager progressManager = ProgressManager.getInstance();
        final DataLayer data = DataLayer.getInstance();

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                progressManager.getProgressIndicator().setText("Save task's data");
                data.commitChangedTaskData();
            }},
            "Save task's data",
            false,
            ideaProject
        );

        ActionManager.getInstance().getAction("V1.toolRefresh").actionPerformed(e);
    }
}
