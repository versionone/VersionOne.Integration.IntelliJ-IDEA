/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;
import com.versionone.integration.idea.TasksComponent;

import java.io.File;

public class Help extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final PluginId id = PluginManager.getPluginByClassName(TasksComponent.class.getCanonicalName());
        final File path = PluginManager.getPlugin(id).getPath();
        final String url = BrowserUtil.getDocURL(path.getPath());
        BrowserUtil.launchBrowser(url + "/doc/V1IntelliJPlugIn/V1IntelliJPlugIn.html");
    }
}
