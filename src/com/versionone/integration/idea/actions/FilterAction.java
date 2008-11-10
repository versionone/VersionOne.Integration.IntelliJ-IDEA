/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Application;
import com.versionone.integration.idea.FilterForm;
import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.FilterComponent;
import com.versionone.common.sdk.IProjectTreeNode;
import org.apache.log4j.Logger;

public class FilterAction extends AnAction {

    private final Logger LOG = Logger.getLogger(this.getClass());

    public void actionPerformed(AnActionEvent e) {
        Application application = ApplicationManager.getApplication();
//        FilterComponent filterComponent =
//                application.getComponent(FilterComponent.class);
        FilterComponent.setupProject(e);
    }
}
