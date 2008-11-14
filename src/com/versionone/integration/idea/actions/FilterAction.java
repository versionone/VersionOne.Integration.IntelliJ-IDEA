/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.FilterForm;
import com.versionone.integration.idea.ProjectTreeNode;
import org.apache.log4j.Logger;

import java.net.ConnectException;

public class FilterAction extends AnAction {

    private static final Logger LOG = Logger.getLogger(FilterAction.class);

    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        if (ideaProject != null && filterDialog(ideaProject)) {
            //DataLayer.getInstance().refresh();
            ActionManager.getInstance().getAction("V1.toolRefresh").actionPerformed(e);
            //final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
            //tc.updateDisplayName();
        }
    }

    public static boolean filterDialog(final Project ideaProject) {

        final ProgressManager progressManager = ProgressManager.getInstance();
        final ProjectTreeNode[] projectsRoot = new ProjectTreeNode[1];
        final boolean[] isError = new boolean[]{true};

//        new Task.Modal(ideaProject, "Loading project list", false) {

//            public void run(ProgressIndicator indicator) {
//                indicator.setFraction(0.000001);
//
////                for (Double i=0.001; i<1; i += 0.001) {
////                    try {
////                        Thread.sleep(1);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
////                    }
////
////                }
//                projectsRoot[0] = DataLayer.getInstance().getProjects();
//            }
//
//        }.queue();

        final DataLayer data;
        data = DataLayer.getInstance();

        boolean isCanceled = ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                final ProgressIndicator indicator = progressManager.getProgressIndicator();
                data.setProgressIndicator(indicator);
                indicator.setText("Loading project list");
                try {
                    projectsRoot[0] = data.getProjects();
                } catch (ConnectException e) {
                    isError[0] = false;
                }
                data.setProgressIndicator(null);
            }
        },
                "Loading project list",
                true,
                ideaProject
        );

        if (!isCanceled) {
            return false;
        }

        if (!isError[0]) {
            Messages.showMessageDialog(
                    "Error connection to the VesionOne server",
                    "Error",
                    Messages.getErrorIcon());
            return false;
        }

        final FilterForm form = new FilterForm(projectsRoot[0]);
        return ShowSettingsUtil.getInstance().editConfigurable(ideaProject, form);
    }
}
