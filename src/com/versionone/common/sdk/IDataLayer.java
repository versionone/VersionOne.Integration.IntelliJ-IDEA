/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.sdk;

import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;

public interface IDataLayer {
    void refresh() throws ConnectException;

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
    ProjectTreeNode getProjects() throws ConnectException;

    boolean isTaskDataChanged(int task);

//    boolean isTaskPropertyChanged(int task, TasksProperties property);
}
