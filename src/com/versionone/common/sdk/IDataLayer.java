/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.sdk;

import com.versionone.integration.idea.V1PluginException;
import org.jetbrains.annotations.NotNull;

import java.util.Vector;

public interface IDataLayer {
    void refresh() throws V1PluginException;

    /**
     * @throws IllegalStateException if trying to commit Efforts when EffortTracking disabled.
     */
    void commitChangedTaskData() throws Exception;

    int getTasksCount();

    String getTaskPropertyValue(int task, TasksProperties property);

    Vector<String> getAvailableValues(int task, TasksProperties property);

    boolean isTrackEffort();

    String[] getAllStatuses();

    void setTaskPropertyValue(int task, TasksProperties property, String value);

    @NotNull
    ProjectTreeNode getProjects() throws V1PluginException;

    boolean isTaskDataChanged(int task);

//    boolean isTaskPropertyChanged(int task, TasksProperties property);

    void reconnect() throws V1PluginException;

    boolean isConnectionValid(String path, String userName, String password);
}
