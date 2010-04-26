/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.ValidatorException;
import com.versionone.common.sdk.Workitem;
import com.versionone.integration.idea.CloseWorkitemDialog;
import com.versionone.integration.idea.TasksTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ContextMenuActionListener implements ActionListener {

    private final Workitem item;
    private final TasksTable view;
    private final IDataLayer dataLayer;


    public ContextMenuActionListener(@NotNull Workitem item, @NotNull TasksTable view, @NotNull IDataLayer dataLayer) {
        this.item = item;
        this.view = view;
        this.dataLayer = dataLayer;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if(command.equals(TasksTable.CONTEXT_MENU_QUICK_CLOSE)) {
            quickClose();
        } else if(command.equals(TasksTable.CONTEXT_MENU_CLOSE)) {
            close();
        } else if(command.equals(TasksTable.CONTEXT_MENU_SIGNUP)) {
            signup();
        } else {
            throw new UnsupportedOperationException("This menu action is not supported");
        }
    }

    private void signup() {
        try {
            item.signup();
            view.updateData();
            view.reloadNode(item);
        } catch(DataLayerException ex) {
            displayError(ex.getMessage());
        }
    }

    private void close() {
        JFrame parent = (JFrame) SwingUtilities.getRoot(view);
        CloseWorkitemDialog dialog = new CloseWorkitemDialog(parent, item, dataLayer);
        dialog.setVisible(true);

        try {
            view.updateData();
        } catch(DataLayerException ex) {
            displayError(ex.getMessage());
        }

        view.reloadModel();
    }

    private void quickClose() {
        try {
            item.quickClose();
            view.updateData();
            view.reloadModel();
        } catch(DataLayerException ex) {
            displayError(ex.getMessage());
        } catch(ValidatorException ex) {
            displayError(ex.getMessage());
        }
    }

    private void displayError(String message) {
        Icon icon = Messages.getErrorIcon();
        Messages.showMessageDialog(message, "Error", icon);
    }
}
