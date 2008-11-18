/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.ui.Messages;
import com.versionone.integration.idea.DataLayer;
import com.versionone.apiclient.V1Exception;

import java.net.ConnectException;

/**
 *
 */
public class SaveData extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        System.out.println("Save.actionPerformed()");

        final DataContext dataContext = e.getDataContext();
//        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        final Project ideaProject = DataKeys.PROJECT.getData(dataContext);
        final ProgressManager progressManager = ProgressManager.getInstance();
        final DataLayer data;
        data = DataLayer.getInstance();

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
