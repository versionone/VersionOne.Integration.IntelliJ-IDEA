/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.versionone.common.sdk.Project;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import java.util.List;

public class FilterForm implements Configurable {
    private JTree projectTree;
    private JPanel panel;
    private JCheckBox showAllTasksCheckBox;
    private WorkspaceSettings settings;

    private final List<Project> projectRoots;

    public FilterForm(List<Project> projectRoots, WorkspaceSettings settings) {
        this.projectRoots = projectRoots;
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
        final Project selectedPrj = (Project) projectTree.getLastSelectedPathComponent();
        if (selectedPrj == null) {
            return false;
        }
        final String cfgPrj = settings.projectToken;
        return !cfgPrj.equals(selectedPrj.getId());
    }

    public void apply() throws ConfigurationException {
        final Project node = (Project) projectTree.getLastSelectedPathComponent();
        if (node != null) {
            settings.projectName = node.toString();
            settings.projectToken = node.getId();
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
        projectTree = new JTree(new ProjectsModel(projectRoots));
        projectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(renderer.getDefaultClosedIcon());
        projectTree.setCellRenderer(renderer);
    }
}
