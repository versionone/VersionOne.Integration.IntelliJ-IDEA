package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.versionone.common.sdk.*;
import com.versionone.integration.idea.CloseWorkitemDialog;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.TasksTable;
import org.apache.log4j.Logger;

import javax.swing.*;

public class CloseWorkitemAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(CloseWorkitemAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project ideaProject = resolveProject(e);

        if (ideaProject != null && dataLayer.isConnected()) {
            TasksComponent tc = resolveTasksComponent(e);
            TasksTable table = tc.getTable();
            JFrame parent = (JFrame) SwingUtilities.getRoot(table);
            Workitem item = (Workitem) tc.getCurrentItem();
            CloseWorkitemDialog dialog = new CloseWorkitemDialog(parent, item, dataLayer);
            dialog.setVisible(true);

            try {
                table.updateData();
            } catch(DataLayerException ex) {
                LOG.warn("Failed to close workitem.", ex);
                displayError(ex.getMessage());
            }

            table.reloadModel();
        }       
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        TasksComponent tc = resolveTasksComponent(e);

        boolean enabled = false;

        if(tc != null) {
            Workitem item = (Workitem) tc.getCurrentItem();
            enabled = dataLayer.isConnected() && item != null && item.isPersistent();
        }

        presentation.setEnabled(enabled && getSettings().isEnabled);
    }
}
