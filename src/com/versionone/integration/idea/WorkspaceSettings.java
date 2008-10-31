package com.versionone.integration.idea;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.PersistentStateComponent;


@State(name = "V1PluginSettings", storages = {
        @Storage(id = "other",
                file = "$WORKSPACE_FILE$"
        )})
public class WorkspaceSettings implements PersistentStateComponent<WorkspaceSettings> {
    public String user, passwd;

    public WorkspaceSettings getState() {
        return this;
    }

    public void loadState(WorkspaceSettings state) {
        user = state.user;
        passwd = state.passwd;
    }
}
