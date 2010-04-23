package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.EntityType;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.integration.idea.TasksComponent;

/**
 * Add defect to the VersionOne.
 */
public class AddDefectAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        Project ideaProject = PlatformDataKeys.PROJECT.getData(dataContext);
        if (ideaProject != null) {
            TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
            PrimaryWorkitem newItem = null;
            try {
                newItem = ApiDataLayer.getInstance().createNewPrimaryWorkitem(EntityType.Defect);
            } catch (DataLayerException ex) {}

            tc.refresh();
            tc.update();
            tc.selectNode(newItem);
        }
    }

    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();
        presentation.setEnabled(ApiDataLayer.getInstance().isConnected());
    }
}
