/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

import org.jetbrains.annotations.Nls;

public class FilterForm implements Configurable {
    private JTree projectTree;
    private JPanel panel;
    private JCheckBox showAllTasksCheckBox;

    private final ProjectTreeNode projectRoot;

    public FilterForm(ProjectTreeNode projectRoot) {
        this.projectRoot = projectRoot;
    }

    @Nls
    public String getDisplayName() {
        return "Project Setup";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        return panel;
    }

    public boolean isModified() {
//        System.out.println("FilterForm.isModified");
//        return cfg.projectName.equals(projectTree.getLastSelectedPathComponent());
        return true;
    }

    public void apply() throws ConfigurationException {
        final Object node = projectTree.getLastSelectedPathComponent();
        WorkspaceSettings.getInstance().projectName = node.toString();
    }

    public void reset() {
        //TODO impl selection of current node tree.setSelectionPath();
        projectTree.clearSelection();
    }

    public void disposeUIResources() {
    }

    private void createUIComponents() {
        projectTree = new JTree(projectRoot);
    }
}
