/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.versionone.integration.idea.DataLayer;

/**
 *
 */
public class SaveData extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        System.out.println("Save.actionPerformed()");
        // TODO: insert action logic here
        DataLayer.getInstance().commitChangedTaskData();

        ActionManager.getInstance().getAction("V1.toolRefresh").actionPerformed(e);
    }
}
