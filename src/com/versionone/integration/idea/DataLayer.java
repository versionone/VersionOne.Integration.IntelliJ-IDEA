/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.progress.ProgressIndicator;
import com.versionone.common.sdk.IStatusCodes;
import com.versionone.common.sdk.TaskStatusCodes;
import com.versionone.om.ApiClientInternals;
import com.versionone.om.ApplicationUnavailableException;
import com.versionone.om.Iteration;
import com.versionone.om.Member;
import com.versionone.om.Project;
import com.versionone.om.SDKException;
import com.versionone.om.Task;
import com.versionone.om.V1Instance;
import com.versionone.om.filters.BaseAssetFilter;
import com.versionone.om.filters.ProjectFilter;
import com.versionone.om.filters.TaskFilter;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.Collection;
import java.util.List;

/**
 * This class requests, stores data from VersionOne server and send changed data back.
 */
public final class DataLayer {

    private static final Logger LOG = Logger.getLogger(DataLayer.class);
    private static DataLayer instance;

    private final WorkspaceSettings cfg;

    private V1Instance v1;
    private Member member;
    private IStatusCodes statusList;
    private boolean trackEffort;
    private Object[][] tasksData = new Object[0][];
    private Object[][] defaultTaskData = new Object[0][0];
    private Task[] serverTaskList = new Task[0];

    private ProgressIndicator progressIndicator;

    private DataLayer(WorkspaceSettings workspaceSettings) {
        cfg = workspaceSettings;
        try {
            connect();
            refresh();
        } catch (ConnectException e) {
            // do nothing
        }
    }

    private void connect() throws ConnectException {
        try {
            v1 = new V1Instance(cfg.v1Path, cfg.user, cfg.passwd);
            v1.validate();
            final ApiClientInternals apiClient = v1.getApiClient();
            statusList = new TaskStatusCodes(apiClient.getMetaModel(), apiClient.getServices());
            trackEffort = v1.getConfiguration().effortTrackingEnabled;
            member = v1.get().memberByUserName(cfg.user);
        } catch (Exception e) {
            LOG.error("Error connection to VersionOne", e);
            throw new ConnectException(e.getMessage());
        }
    }

    public void refresh() throws ConnectException {
        System.out.println("DataLayer.refresh() prj=" + cfg.projectName);

        tasksData = new Object[0][];
        defaultTaskData = new Object[0][];
        serverTaskList = new Task[0];

        if (!isConnectionValid()) {
            connect();
        }
        synchronized (v1) {
            final Project project;
            try {
                project = v1.get().projectByName(cfg.projectName);
            } catch (Exception e) {
                LOG.error("Cannot get project from server", e);
                throw new SDKException(e);
            }
            if (project == null) {
                final SDKException ex = new SDKException("There is no project: " + cfg.projectName);
                LOG.error(ex.getMessage(), ex);
                throw ex;
            }

            final TaskFilter filter = new TaskFilter();
            final Collection<Project> childProjects = project.getThisAndAllChildProjects();

            for (Project prj : childProjects) {
                if (prj.isActive()) {
                    filter.project.add(prj);
                }
            }
            filter.getState().add(BaseAssetFilter.State.Active);
            if (!cfg.isShowAllTask) {
                filter.owners.add(member);
            }
            Collection<Task> tasks = v1.get().tasks(filter);
            tasksData = new Object[tasks.size()][TasksProperties.values().length];
            serverTaskList = new Task[tasks.size()];
            int i = 0;
            for (Task task : tasks) {
                final Iteration iteration = task.getParent().getIteration();
                if (iteration != null && iteration.isActive()) {
                    serverTaskList[i] = task;
                    setTaskData(tasksData[i++], task);
                }
            }
            tasksData = copyOf2DArray(tasksData, i);
            serverTaskList = copyOfArray(serverTaskList, i);
            defaultTaskData = copyOf2DArray(tasksData, i);

            System.out.println("=============== Got " + tasks.size() + " tasks, used " + tasksData.length + " ============");
            wr();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] copyOfArray(T[] source, int i) {
        T[] result = (T[]) Array.newInstance(source.getClass().getComponentType(), i);
        System.arraycopy(source, 0, result, 0, i);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[][] copyOf2DArray(@NotNull T[][] source, int i) {
        T[][] res;
        res = (T[][]) Array.newInstance(source.getClass().getComponentType(), i);
        for (int j = 0; j < i; j++) {
            res[j] = copyOfArray(source[j], source[j].length);
        }
        return res;
    }

    private static void setTaskData(Object[] data, Task task) {
        data[TasksProperties.Title.num] = task.getName();
        data[TasksProperties.ID.num] = task.getDisplayID();
        data[TasksProperties.Parent.num] = task.getParent().getName();
        data[TasksProperties.DetailEstimeate.num] = getBigDecimal(task.getDetailEstimate());
        data[TasksProperties.Done.num] = getBigDecimal(task.getDone());
        data[TasksProperties.Effort.num] = getBigDecimal(0D);
        data[TasksProperties.ToDo.num] = getBigDecimal(task.getToDo());
        data[TasksProperties.Status.num] = task.getStatus().getCurrentValue();
    }

    private static BigDecimal getBigDecimal(Double toDo) {
        return toDo == null ? null : BigDecimal.valueOf(toDo).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void commitChangedTaskData() {
        synchronized (v1) {
            for (int i = 0; i < tasksData.length; i++) {
                if (isTaskDataChanged(i)) {
                    updateServerTask(serverTaskList[i], tasksData[i]);
                    serverTaskList[i].save();
                    System.out.println("Saved:" + i);
                }
            }
        }
    }

    private void updateServerTask(Task task, Object[] data) {
        task.setName(data[TasksProperties.Title.num].toString());
        task.setDetailEstimate(getDoubleValue(data[TasksProperties.DetailEstimeate.num]));
        task.createEffort(getDoubleValue(data[TasksProperties.Effort.num]), member);
        task.setToDo(getDoubleValue(data[TasksProperties.ToDo.num]));
        task.getStatus().setCurrentValue(data[TasksProperties.Status.num] != null ? data[TasksProperties.Status.num].toString() : null);
    }

    private Double getDoubleValue(Object data) {
        return data != null ? ((Number) data).doubleValue() : null;
    }

    /**
     * TODO temporary. delete it.
     */
    private void wr() {
        for (Object[] objects : tasksData) {
            for (int i = 0; i < objects.length; i++) {
                System.out.print(objects[i]);
                if (i < objects.length - 1)
                    System.out.print(" \t|");
                else
                    System.out.print("\n");
            }
        }
    }

    public int getTasksCount() {
        synchronized (tasksData) {
            return tasksData.length;
        }
    }

    public Object getTaskPropertyValue(int task, TasksProperties property) {
        synchronized (tasksData) {
            return tasksData[task][property.num];
        }
    }

    public String[] getAllStatuses() {
        return statusList.getDisplayValues();
    }

    public static DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer(WorkspaceSettings.getInstance());
        }
        return instance;
    }

    public void setTaskPropertyValue(int task, TasksProperties property, Object value) {
        Object data = null;
        if (value != null) {
            switch (property.type) {
                case Text:
                    data = value.toString();
                    break;
                case StatusList:
                    if (!value.equals("")) {
                        data = value.toString();
                    }
                    break;
                case Number:
                    if (!value.equals("")) {
                        data = value;
                    }
                    break;
            }
        }

        synchronized (tasksData) {
            tasksData[task][property.num] = data;
        }
    }

    public void setProgressIndicator(@Nullable ProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

    private boolean isConnectionValid() {
        boolean result = true;

        if (v1 != null) {
            try {
                v1.validate();
            } catch (ApplicationUnavailableException e) {
                result = false;
            }
        } else {
            result = false;
        }

        return result;
    }

    @NotNull
    public ProjectTreeNode getProjects() throws ConnectException {
        ProjectFilter filter = new ProjectFilter();
        filter.getState().add(BaseAssetFilter.State.Active);
        Collection<Project> projects;
        ProjectTreeNode treeProjects;

        if (!isConnectionValid()) {
            connect();
        }

        synchronized (v1) {
            try {
                projects = v1.getProjects();
            } catch (Exception e) {
                LOG.error("Can't get projects list.", e);
                throw new SDKException("Can't get projects list.", e);
            }
            Project mainProject = projects.iterator().next();
            treeProjects = new ProjectTreeNode(mainProject.getName(), null, 0, mainProject.getID().getToken());
            recurseAndAddNodes(treeProjects.children, mainProject.getChildProjects(filter), null);
        }

        return treeProjects;
    }

    private void recurseAndAddNodes(List<ProjectTreeNode> projectTreeNodes, Collection<Project> projects, ProjectTreeNode parent) {
        int i = 0;
        for (Project project : projects) {
            ProjectTreeNode oneNode = new ProjectTreeNode(project.getName(), parent, i++, project.getID().getToken());
            projectTreeNodes.add(oneNode);

            ProjectFilter filter = new ProjectFilter();
            filter.getState().add(BaseAssetFilter.State.Active);
            if (progressIndicator != null && progressIndicator.isRunning()) {
                progressIndicator.checkCanceled();
            }
            recurseAndAddNodes(oneNode.children, project.getChildProjects(filter), oneNode);
        }
    }

    public boolean isTaskDataChanged(int task) {
        synchronized (tasksData) {
            for (TasksProperties property : TasksProperties.values()) {
                // skip not editable properties
                if (!property.isEditable) {
                    continue;
                }

                final Object actual = tasksData[task][property.num];
                final Object expected = defaultTaskData[task][property.num];
                if (actual == null) {
                    if (expected != null) {
                        return true;
                    }
                } else {
                    if (!actual.equals(expected)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
