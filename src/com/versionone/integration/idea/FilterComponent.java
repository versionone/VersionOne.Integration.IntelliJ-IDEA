package com.versionone.integration.idea;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.versionone.common.sdk.IProjectTreeNode;

public class FilterComponent {

    public static void setupProject(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        Project prj = (Project) dataContext.getData(DataConstantsEx.PROJECT);
        IProjectTreeNode prj1 = DataLayer.getInstance().getProjects();
        System.out.println("Prepare edit.");
        final FilterForm form = new FilterForm(prj1);
        boolean b = ShowSettingsUtil.getInstance().editConfigurable(prj, form);
        System.out.println("Edited. b="+b);
    }
}