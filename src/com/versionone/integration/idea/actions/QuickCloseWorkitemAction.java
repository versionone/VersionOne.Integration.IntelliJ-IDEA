package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.ValidatorException;
import com.versionone.common.sdk.Workitem;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.TasksTable;
import org.apache.log4j.Logger;

public class QuickCloseWorkitemAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(QuickCloseWorkitemAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project ideaProject = resolveProject(e);

        if (ideaProject != null && dataLayer.isConnected()) {
            TasksComponent tc = resolveTasksComponent(ideaProject);
            TasksTable table = tc.getTable();
            Workitem item = (Workitem) tc.getCurrentItem();

            try {
                item.quickClose();
                table.updateData();
                table.reloadModel();
            } catch(DataLayerException ex) {
                LOG.warn("Failed to quick close workitem.", ex);
                displayError(ex.getMessage());
            } catch(ValidatorException ex) {
                LOG.warn("Failed to quick close workitem.", ex);
                displayError(ex.getMessage());
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        TasksComponent tc = resolveTasksComponent(e);

        boolean enabled = false;

        if(tc != null) {
            Workitem item = (Workitem) tc.getCurrentItem();
            enabled = dataLayer.isConnected() && item != null && item.canQuickClose() && item.isPersistent();
        }

        presentation.setEnabled(enabled && getSettings().isEnabled);
    }
}
