/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.versionone.common.sdk.ProjectTreeNode;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;

public class FilterForm implements Configurable {
    private JTree projectTree;
    private JPanel panel;
    private JCheckBox showAllTasksCheckBox;
    private WorkspaceSettings settings;

    private final ProjectTreeNode projectRoot;

    public FilterForm(ProjectTreeNode projectRoot, WorkspaceSettings settings) {
        this.projectRoot = projectRoot;
        this.settings = settings;
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
        if (settings.isShowAllTask != showAllTasksCheckBox.isSelected()) {
            return true;
        }
        final ProjectTreeNode selectedPrj = (ProjectTreeNode) projectTree.getLastSelectedPathComponent();
        if (selectedPrj == null) {
            return false;
        }
        final String cfgPrj = settings.projectToken;
        return !cfgPrj.equals(selectedPrj.getToken());
    }

    public void apply() throws ConfigurationException {
        final ProjectTreeNode node = (ProjectTreeNode) projectTree.getLastSelectedPathComponent();
        if (node != null) {
            settings.projectName = node.toString();
            settings.projectToken = node.getToken();
        }
        settings.isShowAllTask = showAllTasksCheckBox.isSelected();

        //dataLayer.setSettings(settings);
    }

    public void reset() {
        showAllTasksCheckBox.setSelected(settings.isShowAllTask);
        projectTree.clearSelection();
        projectTree.expandRow(0);//TODO make current project selected
    }

    public void disposeUIResources() {
    }

    private void createUIComponents() {
        projectTree = new JTree(projectRoot);
        projectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }
}
