package com.versionone.integration.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.project.Project;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.IDataLayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class AbstractComponent implements ProjectComponent {
    private final Project project;
    private final IDataLayer dataLayer;
    private WorkspaceSettings settings;

    public AbstractComponent(Project project, WorkspaceSettings settings) {
        this.project = project;
        this.settings = settings;
        dataLayer = ApiDataLayer.getInstance();
    }

    public abstract void projectOpened();

    public void projectClosed() {}

    public abstract void initComponent();

    public void disposeComponent() {}

    abstract void registerTool();

    abstract void unregisterTool();

    abstract void registerTableListener();

    abstract void update();

    public Project getProject() {
        return project;
    }

    @NotNull
    IDataLayer getDataLayer(){
        if (dataLayer == null) {
            throw new IllegalStateException("method call before creating object");
        }
        return dataLayer;
    }

    public WorkspaceSettings getSettings() {
        return settings;
    }
}
