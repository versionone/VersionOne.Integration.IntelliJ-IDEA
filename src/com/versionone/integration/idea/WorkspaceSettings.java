package com.versionone.integration.idea;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;


@State(name = "v1ideaplugin.settings", storages = @Storage(id = "ws", file = "$WORKSPACE_FILE$"))
public class WorkspaceSettings implements PersistentStateComponent<WorkspaceSettings> {
    public String v1Path = "http://localhost/VersionOne/";//http://jsdksrv01/VersionOne/
    public String user = "";
    public String passwd = "";
    public String projectName = "";//V1EclipseTestPrj
    public String projectToken = "";//Scope:2689
    public boolean isShowAllTask = true;
    public boolean isWindowsIntegratedAuthentication = false;

    public WorkspaceSettings getState() {
        return this;
    }

    public void loadState(WorkspaceSettings state) {
        user = state.user;
        passwd = state.passwd;
        v1Path = state.v1Path;
        projectName = state.projectName;
        isShowAllTask = state.isShowAllTask;
        projectToken = state.projectToken;
    }
}
