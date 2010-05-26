/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.ValidatorException;
import com.versionone.integration.idea.DetailsComponent;
import com.versionone.integration.idea.ValidationResultDialog;
import com.versionone.integration.idea.TasksComponent;
import org.apache.log4j.Logger;

import javax.swing.*;

public class SaveDataAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(SaveDataAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project ideaProject = resolveProject(e);

        if (ideaProject == null) {            
            return;
        }
        final TasksComponent tc = resolveTasksComponent(ideaProject);
        final DetailsComponent dc = resolveDetailsComponent(ideaProject);
        final ProgressManager progressManager = ProgressManager.getInstance();

        // is exception, text of exception, error or warning, display in standard or in custom dialog
        final Object[] isError = {false, "", false, false};
        tc.removeEdition();
        dc.removeEdition();

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                progressManager.getProgressIndicator().setText("Saving changes to VersionOne");
                try {
                    dataLayer.commitChanges();
                } catch (DataLayerException ex) {
                    isError[0] = true;
                    isError[1] = ex.getMessage();
                    isError[2] = true; //ex.isError();
                    LOG.warn(isError[1], ex);
                } catch(ValidatorException ex) {
                    isError[0] = true;
                    isError[1] = ex.getMessage();
                    isError[2] = true;
                    isError[3] = true;
                    LOG.warn(isError[1], ex);
                } catch (Exception ex) {//TODO should we handle this exception?
                    isError[0] = true;
                    isError[1] = "Error connecting to VersionOne";
                    isError[2] = true;
                    LOG.warn(isError[1], ex);
                }
            }
        },
                "Save changes to VersionOne",
                false,
                ideaProject
        );

        if ((Boolean) isError[3]) {
            ValidationResultDialog dialogForRequiredFields = new ValidationResultDialog((String) isError[1]);
            dialogForRequiredFields.setVisible(true);
            return;
        }

        if ((Boolean) isError[0]) {
            Icon icon = (Boolean) isError[2] ? Messages.getErrorIcon() : Messages.getWarningIcon();
            Messages.showMessageDialog(isError[1].toString(), "Error", icon);
            return;
        }

        ActionManager.getInstance().getAction("V1.toolRefresh").actionPerformed(e);
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(dataLayer.isConnected() && getSettings().isEnabled);
    }
}
