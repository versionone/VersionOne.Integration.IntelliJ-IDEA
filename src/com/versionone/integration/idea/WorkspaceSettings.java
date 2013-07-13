package com.versionone.integration.idea;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;


@State(name = "v1ideaplugin.settings", storages = @Storage(id = "ws", file = "$WORKSPACE_FILE$"))
public class WorkspaceSettings implements PersistentStateComponent<WorkspaceSettings> {
    public String v1Path = "http://localhost/VersionOne/";//http://integsrv01/VersionOne/
    public String user = "admin";
    public String passwd = "admin";
    public String projectName = "";//V1EclipseTestPrj
    public String projectToken = "";//Scope:2689
    public boolean isShowAllTask = true;
    public boolean isWindowsIntegratedAuthentication = false;
    public boolean isEnabled = false;
    public boolean isProxyEnabled = false;
    public String proxyUsername = "";
    public String proxyPassword = "";
    public String proxyUri = "";

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
        isEnabled = state.isEnabled;
        isProxyEnabled = state.isProxyEnabled;
        proxyUsername = state.proxyUsername;
        proxyPassword = state.proxyPassword;
        proxyUri = state.proxyUri;
    }
}
