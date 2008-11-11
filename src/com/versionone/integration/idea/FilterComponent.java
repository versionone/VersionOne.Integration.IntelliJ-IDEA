/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Application;

import javax.swing.*;
import java.awt.*;

import org.apache.log4j.Logger;

public class FilterComponent {

    private static final Logger LOG = Logger.getLogger(FilterComponent.class);

    public static void setupProject(AnActionEvent e) {
        try {
            final DataContext dataContext = e.getDataContext();
            final Project ideaProject = (Project) dataContext.getData(DataConstantsEx.PROJECT);
            final ProjectTreeNode projectsRoot = DataLayer.getInstance().getProjects();
            final FilterForm form = new FilterForm(projectsRoot);
            final boolean changed = ShowSettingsUtil.getInstance().editConfigurable(ideaProject, form);
            if (changed) {
                DataLayer.getInstance().refresh();
                final TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
                tc.updateDisplayName();
            }
        } catch (NullPointerException e1) {
            LOG.error(e1);
        }
    }

    /**
     * Temporary method for testing purposes. TODO delete
     */
    public static void main(String[] args) {
        TasksComponent plugin = new TasksComponent(null);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JTree(DataLayer.getInstance().getProjects()));
        JFrame frame = new JFrame("IDEA V1 Plugin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(200, 800));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}