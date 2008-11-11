/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;

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
        if (WorkspaceSettings.getInstance().isShowAllTask != showAllTasksCheckBox.isSelected()) {
            return true;
        }
        final Object selectedPrj = projectTree.getLastSelectedPathComponent();
        if (selectedPrj == null) {
            return false;
        }
        final String cfgPrj = WorkspaceSettings.getInstance().projectName;
        return !cfgPrj.equals(selectedPrj.toString());//TODO change to use IDs
    }

    public void apply() throws ConfigurationException {
        final Object node = projectTree.getLastSelectedPathComponent();
        if (node != null) {
            WorkspaceSettings.getInstance().projectName = node.toString();
        }
        WorkspaceSettings.getInstance().isShowAllTask = showAllTasksCheckBox.isSelected();
    }

    public void reset() {
        showAllTasksCheckBox.setSelected(WorkspaceSettings.getInstance().isShowAllTask);
        projectTree.clearSelection();
    }

    public void disposeUIResources() {
    }

    private void createUIComponents() {
        projectTree = new JTree(projectRoot);
        projectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }
}
