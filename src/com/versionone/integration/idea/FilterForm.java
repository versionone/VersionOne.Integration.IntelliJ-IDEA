/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.List;

public class FilterForm implements Configurable {

    private final Logger LOG = Logger.getInstance(this.getClass().getSimpleName());
    private final ProjectsModel model;
    private final WorkspaceSettings settings;

    private JTree projectTree;
    private JPanel panel;

    private final IDataLayer dataLayer;

    public FilterForm(@NotNull List<Project> rootProjects, @NotNull WorkspaceSettings settings, @NotNull IDataLayer dataLayer) {
        this.dataLayer = dataLayer;
        this.settings = settings;
        model = new ProjectsModel(rootProjects);
        projectTree.setModel(model);
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
        final Object selectedPrj = projectTree.getLastSelectedPathComponent();
        if (!(selectedPrj instanceof ProjectsModel.ProjectWrapper)) {
            LOG.debug("Wrong selected project:" + selectedPrj);
            return false;
        }
        String newPrj = ((ProjectsModel.ProjectWrapper) selectedPrj).id;
        final String oldPrj = settings.projectToken;
        return !oldPrj.equals(newPrj);
    }

    public void apply() throws ConfigurationException {
        final ProjectsModel.ProjectWrapper node =
                (ProjectsModel.ProjectWrapper) projectTree.getLastSelectedPathComponent();
        if (node != null) {
            settings.projectName = node.name;
            settings.projectToken = node.id;
            dataLayer.setCurrentProjectId(node.id);
        }
    }

    public void reset() {
        projectTree.clearSelection();
        projectTree.expandRow(0);
        final TreePath path = model.getPathById(settings.projectToken);
        if (path != null) {
            projectTree.setSelectionPath(path);
        }
    }

    public void disposeUIResources() {
    }

    private void createUIComponents() {
        projectTree = new JTree();
        projectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(renderer.getDefaultClosedIcon());
        projectTree.setCellRenderer(renderer);
    }
}
