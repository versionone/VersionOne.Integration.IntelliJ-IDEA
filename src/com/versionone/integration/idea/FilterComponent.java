package com.versionone.integration.idea;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.options.ShowSettingsUtil;

import javax.swing.*;
import java.awt.*;

public class FilterComponent {

    public static void setupProject(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        Project prj = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        ProjectTreeNode prj1 = DataLayer.getInstance().getProjects();
        System.out.println("Prepare edit.");
        final FilterForm form = new FilterForm(prj1);
        boolean b = ShowSettingsUtil.getInstance().editConfigurable(prj, form);
        System.out.println("Edited. b="+b);
    }


    /**
     * Temporary method for testing purposes.
     */
    public static void main(String[] args) {
        TasksComponent plugin = new TasksComponent(null);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JTree(DataLayer.getInstance().getProjects()));
        JFrame frame = new JFrame("IDEA V1 Plugin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 100));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}