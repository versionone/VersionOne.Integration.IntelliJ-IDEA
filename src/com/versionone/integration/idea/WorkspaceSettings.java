package com.versionone.integration.idea;

/*
@State(name = "V1PluginSettings", storages = {
        @Storage(id = "other",
                file = "$WORKSPACE_FILE$"
        )})
*/
public class WorkspaceSettings {//implements PersistentStateComponent<WorkspaceSettings> {
    public String v1Path = "http://jsdksrv01/VersionOne/";
    public String user = "admin";
    public String passwd = "admin";
    public String projectName = "V1EclipseTestPrj";
    public boolean isShowAllTask = true;

    private static WorkspaceSettings setting = new WorkspaceSettings();

    // singelton
    private WorkspaceSettings() {
    }

    public WorkspaceSettings getState() {
        return this;
    }

    public void loadState(WorkspaceSettings state) {
        user = state.user;
        passwd = state.passwd;
        v1Path = state.v1Path;
        projectName = state.projectName;
        isShowAllTask = state.isShowAllTask;
    }

    public static WorkspaceSettings getInstance() {
        return setting;
    }
}
