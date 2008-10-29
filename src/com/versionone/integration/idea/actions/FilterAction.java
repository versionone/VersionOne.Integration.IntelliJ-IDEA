/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.apache.log4j.Logger;

public class FilterAction extends ToggleAction {

    private final Logger LOG = Logger.getLogger(this.getClass());
    private boolean state;

    public boolean isSelected(AnActionEvent e) {
        return state;
    }

    public void setSelected(AnActionEvent e, boolean state) {
        this.state = state;
    }
}
