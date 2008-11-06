/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.versionone.integration.idea.DataLayer;

public class Refresh extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        System.out.println("Refresh.actionPerformed()");
        DataLayer.getInstance().refresh();
//        ApplicationManager.
        // TODO: insert action logic here
    }
}
