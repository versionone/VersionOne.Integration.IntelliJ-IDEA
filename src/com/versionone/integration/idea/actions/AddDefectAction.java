/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.EntityType;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.integration.idea.TasksComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * Create in-memory Defect. New entity will be persisted when user triggers Save action.
 */
public class AddDefectAction extends AnAction {

    private IDataLayer dataLayer;

    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        Project ideaProject = PlatformDataKeys.PROJECT.getData(dataContext);
        if (ideaProject != null && dataLayer.isConnected()) {
            TasksComponent tc = ideaProject.getComponent(TasksComponent.class);
            PrimaryWorkitem newItem = null;
            try {
                newItem = dataLayer.createNewPrimaryWorkitem(EntityType.Defect);
            } catch (DataLayerException ex) {
                Icon icon = Messages.getErrorIcon();
                Messages.showMessageDialog("Failed to create new " + EntityType.Defect.name(), "Error", icon);
            }

            tc.refresh();
            tc.update();
            tc.selectNode(newItem);
        }
    }

    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();
        presentation.setEnabled(dataLayer.isConnected());
    }

    public void setDataLayer(@NotNull IDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }
}
