/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.apiclient.V1Exception;
import com.versionone.common.sdk.IStatusCodes;
import com.versionone.common.sdk.TaskStatusCodes;
import com.versionone.om.ApiClientInternals;
import com.versionone.om.IListValueProperty;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Requests, cache, get change requests and store data from VersionOne server.
 */
public final class DataLayer {
    private static final Logger LOG = Logger.getLogger(DataLayer.class);

    private static DataLayer instance;

    private Object[][] defaultTaskData;
    private final WorkspaceSettings cfg;

    private V1Instance v1;
    private Member member;
    private IStatusCodes statusList;
    private boolean trackEffort;
    private Object[][] tasksData;
    private Task[] serverTaskList;

    private DataLayer(WorkspaceSettings workspaceSettings) {
        cfg = workspaceSettings;
        try {
            v1 = new V1Instance(cfg.v1Path, cfg.user, cfg.passwd);
            final ApiClientInternals apiClient = v1.getApiClient();
            statusList = new TaskStatusCodes(apiClient.getMetaModel(), apiClient.getServices());
            trackEffort = v1.getConfiguration().effortTrackingEnabled;
            member = v1.get().memberByUserName(cfg.user);//TODO cache
        } catch (V1Exception e) {
            e.printStackTrace();
        }
        refresh();
    }

    public synchronized void refresh() {
        System.out.println("DataLayer.refresh() prj=" + cfg.projectName);

        final Project project;
        try {
            project = v1.get().projectByName(cfg.projectName);
        } catch (SDKException e) {
            LOG.error("Error on SDK level", e);
            return;
        }
        if (project == null) {
            LOG.error("There is no project: " + cfg.projectName);
            return;
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
        tasksData = new Object[tasks.size()][TasksProperties.COUNT];
        serverTaskList = new Task[tasks.size()];
        int i = 0;
        for (Task task : tasks) {
            final Iteration iteration = task.getParent().getIteration();
            if (iteration != null && iteration.isActive()) {
                serverTaskList[i] = task; 
                setTaskData(tasksData[i++], task);
            }
        }
        tasksData = Arrays.copyOf(tasksData, i);
        serverTaskList = Arrays.copyOf(serverTaskList, i);

        saveDefaultTaskData();

        System.out.println("=============== Got " + tasks.size() + " tasks, used " + tasksData.length + " ============");
        wr();
    }

    private static void setTaskData(Object[] data, Task task) {
        data[TasksProperties.Title.getNum()] = task.getName();
        data[TasksProperties.ID.getNum()] = task.getID();
        data[TasksProperties.Parent.getNum()] = task.getParent().getName();
        data[TasksProperties.DetailEstimeate.getNum()] = task.getDetailEstimate();
        data[TasksProperties.Done.getNum()] = task.getDone();
        data[TasksProperties.Effort.getNum()] = 0;
        data[TasksProperties.ToDo.getNum()] = task.getToDo();
        final IListValueProperty status = task.getStatus();
        data[TasksProperties.Status.getNum()] = status.getCurrentValue();
    }

    private void saveDefaultTaskData() {
        // TODO defaultTaskData = tasksData.clone();
        if (tasksData.length > 0) {
            defaultTaskData = new Object[tasksData.length][tasksData[0].length];

            for (int i = 0; i < tasksData.length; i++) {
                for (int j = 0; j < tasksData[i].length; j++) {
                    defaultTaskData[i][j] = tasksData[i][j];
                }
            }
        }
    }

    public void commitChangedTaskData() {

        //v1.get

        for (int i = 0; i < tasksData.length; i++) {
            if(isTaskDataChanged(i)) {
                updateServerTask(serverTaskList[i], tasksData[i]);
                serverTaskList[i].save();
            }
        }

    }

    private void updateServerTask(Task task, Object[] data) {
        task.setName(data[TasksProperties.Title.getNum()].toString());
        //data[TasksProperties.ID.getNum()] = task.getID();
        //data[TasksProperties.Parent.getNum()] = task.getParent().getName();
        task.setDetailEstimate(getDoubleValue(data[TasksProperties.DetailEstimeate.getNum()]));
        //data[TasksProperties.Done.getNum()] = task.getDone();
        task.createEffort(getDoubleValue(data[TasksProperties.Effort.getNum()].toString()) , member);
        task.setToDo(getDoubleValue(data[TasksProperties.ToDo.getNum()]));
        //final IListValueProperty status = task.getStatus();
        //data[TasksProperties.Status.getNum()] = status.getCurrentValue();
        task.getStatus().setCurrentValue(data[TasksProperties.Status.getNum()] != null ? data[TasksProperties.Status.getNum()].toString() : null);
    }

    private Double getDoubleValue(Object data) {
        return data != null ? Double.parseDouble(data.toString()) : null;
    }

    public synchronized void setNewTaskValue(int task, TasksProperties property) {
        System.out.print("Id=" + task);
        if (property != null) {
            System.out.println(" // name=" + property.name());
        }
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

    public synchronized int getTasksCount() {
        return tasksData.length;
    }

    public synchronized Object getTaskPropertyValue(int task, TasksProperties property) {
        return tasksData[task][property.getNum()];
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

    public synchronized void setTaskPropertyValue(int task, TasksProperties property, Object value) {
        Object data = null;
        if (value != null) {
            switch (property.getType()) {
                case String:
                    data = value.toString();
                    break;
                case StatusList:
                    if (!value.equals("")) {
                        data = value.toString();
                    }
                    break;
                case Number:
                    if (!value.equals("")) {
                        data = Double.parseDouble(value.toString());
                    }
                    break;
            }
        }

        tasksData[task][property.getNum()] = data;
    }

    @NotNull
    public synchronized ProjectTreeNode getProjects() {
        ProjectFilter filter = new ProjectFilter();
        filter.getState().add(BaseAssetFilter.State.Active);
        Collection<Project> projects = v1.getProjects();

        Project mainProject = projects.iterator().next();

        ProjectTreeNode treeProjects = new ProjectTreeNode(mainProject.getName(), null, 0, mainProject.getID().getToken());

        /*
        Collection<Project> projects = v1.getProjects().iterator().next().getChildProjects(filter, true);
        for(Project project : projects) {
            getAllChildren(project, projects);
        }
        */

        recurseAndAddNodes(treeProjects.children, mainProject.getChildProjects(filter), null);

        return treeProjects;
    }

    private void recurseAndAddNodes(List<ProjectTreeNode> projectTreeNodes, Collection<Project> projects, ProjectTreeNode parent) {
        int i = 0;
        for (Project project : projects) {
            ProjectTreeNode oneNode = new ProjectTreeNode(project.getName(), parent, i++, project.getID().getToken());
            projectTreeNodes.add(oneNode);

            ProjectFilter filter = new ProjectFilter();
            filter.getState().add(BaseAssetFilter.State.Active);
            recurseAndAddNodes(oneNode.children, project.getChildProjects(filter), oneNode);
        }
    }


    public synchronized boolean isTaskDataChanged(int task) {
        boolean result = true;

        for (TasksProperties property : TasksProperties.values()) {

            // if property is not editable - next iteration
            if (!property.isEditable()) {
                continue;
            }

            if (tasksData[task][property.getNum()] != null &&
                    defaultTaskData[task][property.getNum()] != null) {
                switch (property.getType()) {
                    case Number:
                        Double editedNumber = Double.parseDouble(tasksData[task][property.getNum()].toString());
                        Double defaultNumber = Double.parseDouble(defaultTaskData[task][property.getNum()].toString());
                        result = result && editedNumber.equals(defaultNumber);
                        break;
                    case StatusList:
                    case String:
                        String editedData = tasksData[task][property.getNum()].toString();
                        String defaultData = defaultTaskData[task][property.getNum()].toString();
                        result = result && editedData.equals(defaultData);
                        break;
                }
            } else if (tasksData[task][property.getNum()] == null &&
                    defaultTaskData[task][property.getNum()] == null) {
                result = true;
            } else {
                result = false;
            }

            if (!result) {
                break;
            }
        }

        return !result;
    }
}
