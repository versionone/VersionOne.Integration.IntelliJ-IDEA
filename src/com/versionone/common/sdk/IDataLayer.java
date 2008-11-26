/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.sdk;

import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;

import com.versionone.integration.idea.V1PluginException;

public interface IDataLayer {
    void refresh() throws V1PluginException;

    /**
     * @throws IllegalStateException if trying to commit Efforts when EffortTracking disabled.
     */
    void commitChangedTaskData() throws Exception;

    int getTasksCount();

    Object getTaskPropertyValue(int task, TasksProperties property);

    boolean isTrackEffort();

    String[] getAllStatuses();

    void setTaskPropertyValue(int task, TasksProperties property, Object value);

    @NotNull
    ProjectTreeNode getProjects() throws V1PluginException;

    boolean isTaskDataChanged(int task);

//    boolean isTaskPropertyChanged(int task, TasksProperties property);

    void reconnect() throws V1PluginException;

    boolean isConnectionValid(String path, String userName, String password);
}
