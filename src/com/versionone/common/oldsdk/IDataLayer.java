/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.oldsdk;

import com.versionone.integration.idea.V1PluginException;
import com.versionone.Oid;
import com.versionone.common.oldsdk.TasksProperties;
import com.versionone.common.oldsdk.ProjectTreeNode;
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

    boolean isTrackEffort();

    void setTaskPropertyValue(int task, TasksProperties property, String value);

    @NotNull
    ProjectTreeNode getProjects() throws V1PluginException;

    boolean isTaskDataChanged(int task);

    boolean isPropertyChanged(int task, TasksProperties property);

    void reconnect() throws V1PluginException;

    boolean isConnectionValid(String path, String userName, String password);

    boolean isTaskChanged();

    Vector<String> getPropertyValues(TasksProperties property);

    Oid getPropertyValueOid(String value, TasksProperties property);
}
