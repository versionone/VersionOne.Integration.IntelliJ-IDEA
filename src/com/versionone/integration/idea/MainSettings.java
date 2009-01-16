/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MainSettings implements ApplicationComponent, Configurable {
    private ConfigForm form;
    private WorkspaceSettings settings;
    private Project project;

    public MainSettings(Project project, WorkspaceSettings settings) {
        this.settings = settings;
        this.project = project;
    }

    public void initComponent() {
        //loadRegisteredData();
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "Settings";
    }

    public String getDisplayName() {
        return "VersionOne";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (form == null) {
            form = new ConfigForm(settings, project);
        }
        return form.getPanel();
    }

    public boolean isModified() {
        return form == null || form.isModified();
    }

    public void apply() throws ConfigurationException {
        if (form != null) {
            if (form.isConnectVerified() && form.isConnectValid()) {
                // Get data from form to component
                form.apply();
                final TasksComponent tc = project.getComponent(TasksComponent.class);
                final DetailsComponent dc = project.getComponent(DetailsComponent.class);
                tc.update();
                dc.update();
            }
            else {
                throw new ConfigurationException("Connection has not been validated or contains invalid values");
            }

        }
    }

    public void reset() {
        if (form != null) {
            // Reset form data from component
            form.reset();
        }
    }

    public void disposeUIResources() {
        form = null;
    }

}

