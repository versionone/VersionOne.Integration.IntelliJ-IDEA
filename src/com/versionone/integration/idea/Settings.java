package com.versionone.integration.idea;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.PersistentStateComponent;

/**
 * Created by IntelliJ IDEA.
*/
@State(name = "V1PluginSettings", storages = {
        @Storage(id = "other",
                file = "$WORKSPACE_FILE$"
        )})
public class Settings implements PersistentStateComponent<Settings> {
    public String user, passwd;

    public Settings getState() {
        return this;
    }

    public void loadState(Settings state) {
        user = state.user;
        passwd = state.passwd;
    }
}
