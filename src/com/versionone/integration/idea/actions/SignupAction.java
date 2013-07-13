package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.Workitem;
import com.versionone.integration.idea.TasksComponent;
import com.versionone.integration.idea.TasksTable;
import org.apache.log4j.Logger;

public class SignupAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(SignupAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project ideaProject = resolveProject(e);

        if (ideaProject != null && dataLayer.isConnected()) {
            TasksComponent tc = resolveTasksComponent(ideaProject);
            TasksTable table = tc.getTable();
            Workitem item = (Workitem) tc.getCurrentItem();
            try {
                item.signup();
                table.updateData();
                table.reloadNode(item);
            } catch(DataLayerException ex) {
                LOG.warn("Failed to signup.", ex);
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
            enabled = isEnabledForWorkitem(item);
        }

        presentation.setEnabled(enabled && dataLayer.isConnected() && getSettings().isEnabled);
    }

    private boolean isEnabledForWorkitem(Workitem item) {
        return item != null && item.isPersistent() && item.canSignup() && !item.isMine();
    }
}
