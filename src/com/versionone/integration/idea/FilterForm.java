/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.versionone.common.sdk.IProjectTreeNode;

import javax.swing.*;

import org.jetbrains.annotations.Nls;

public class FilterForm implements Configurable {
    private JTree projectTree;
    private JPanel panel;
    private JCheckBox showAllTasksCheckBox;

    private IProjectTreeNode myProject;

    public FilterForm(IProjectTreeNode myProject) {
        this.myProject = myProject;
    }

    @Nls
    public String getDisplayName() {
        return "Project Setup";
    }

    public Icon getIcon() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getHelpTopic() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public JComponent createComponent() {
        return panel;
    }

    public boolean isModified() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void apply() throws ConfigurationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void disposeUIResources() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void createUIComponents() {
        projectTree = new JTree(new ProjectTreeNode(myProject, null, -1));
    }
}
