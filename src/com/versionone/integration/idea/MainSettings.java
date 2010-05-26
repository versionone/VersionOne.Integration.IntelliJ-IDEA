/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MainSettings implements ApplicationComponent, Configurable {
    private ConfigForm form;
    private WorkspaceSettings settings;
    private Project project;
    private ToolComponent component;

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
        component = new ToolComponent(project.getComponent(TasksComponent.class),
                                                    project.getComponent(DetailsComponent.class));
        if (form == null) {
            form = new ConfigForm(settings, component.getDataLayer());
        }
        return form.getPanel();
    }

    public boolean isModified() {
        return form == null || form.isModified();
    }

    public void apply() throws ConfigurationException {
        if (form != null) {
            if (form.isConnectionVerified()) {
                // Get data from form to component
                form.apply();
                if (settings.isEnabled) {
                    component.registerTool();
                    component.update();
                } else {
                    component.unregisterTool();
                }
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

