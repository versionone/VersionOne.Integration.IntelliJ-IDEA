/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.ui.Messages;
import com.versionone.apiclient.V1Exception;
import com.versionone.apiclient.MetaException;
import com.versionone.common.sdk.IStatusCodes;
import com.versionone.common.sdk.TaskStatusCodes;
import com.versionone.om.*;
import com.versionone.om.filters.BaseAssetFilter;
import com.versionone.om.filters.ProjectFilter;
import com.versionone.om.filters.TaskFilter;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.math.BigDecimal;
import java.net.ConnectException;

/**
 * Requests, cache, get change requests and store data from VersionOne server.
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

    private boolean connect() throws ConnectException {
        boolean result = false;

        try {
            v1 = new V1Instance(cfg.v1Path, cfg.user, cfg.passwd);
            v1.validate();
            final ApiClientInternals apiClient = v1.getApiClient();
            statusList = new TaskStatusCodes(apiClient.getMetaModel(), apiClient.getServices());
            trackEffort = v1.getConfiguration().effortTrackingEnabled;
            member = v1.get().memberByUserName(cfg.user);//TODO cache
            result = true;
        } catch (Exception e) {
//            Messages.showMessageDialog(
//                    e.getMessage(),
//                    "Error",
//                    Messages.getErrorIcon());
            LOG.error("Error connection to VersionOne", e);
            throw new ConnectException(e.getMessage());
        }
//        } catch (V1Exception e) {
////            Messages.showMessageDialog(
////                    "Error connection to VersionOne:\n" + e.getMessage(),
////                    "Error",
////                    Messages.getErrorIcon());
//            LOG.error("Error connection to VersionOne", e);
//            throw new ConnectException(e.getMessage());
//        } catch (MetaException e) {
////            Messages.showMessageDialog(
////                    "Error connection to VersionOne:\n" + e.getMessage(),
////                    "Error",
////                    Messages.getErrorIcon());
//            LOG.error("Error connection to VersionOne", e);
//            throw new ConnectException(e.getMessage());
//        }

        return result;
    }

    public void refresh() throws ConnectException {
        System.out.println("DataLayer.refresh() prj=" + cfg.projectName);

        tasksData = new Object[0][0];
        defaultTaskData = new Object[0][0];
        serverTaskList = new Task[0];

        if (!isConnectionValid()) {
            connect();
        }
        synchronized (v1) {
            final Project project;
            try {
                project = v1.get().projectByName(cfg.projectName);
            } catch (Exception e) {
//                Messages.showMessageDialog(
//                        "Can't get '" + cfg.projectName + "' project:\n" + e.getMessage(),
//                        "Error",
//                        Messages.getErrorIcon());
                LOG.error("Error on SDK level", e);
                throw new SDKException(e);
            }// catch (MetaException e) {
////                Messages.showMessageDialog(
////                        "Can't get '" + cfg.projectName + "' project:\n" + e.getMessage(),
////                        "Error",
////                        Messages.getErrorIcon());
//                LOG.error("Error on SDK level", e);
//                return;
//            }
            if (project == null) {
                Messages.showMessageDialog(
                        "There is no project:" + cfg.projectName,
                        "Error",
                        Messages.getErrorIcon());
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
            tasksData = Arrays.copyOf(tasksData, i);
            serverTaskList = Arrays.copyOf(serverTaskList, i);

            saveDefaultTaskData();

            System.out.println("=============== Got " + tasks.size() + " tasks, used " + tasksData.length + " ============");
            wr();
        }
    }

    private static void setTaskData(Object[] data, Task task) {
        data[TasksProperties.Title.num] = task.getName();
        data[TasksProperties.ID.num] = task.getDisplayID();
        data[TasksProperties.Parent.num] = task.getParent().getName();
        data[TasksProperties.DetailEstimeate.num] = getBigDecimal(task.getDetailEstimate());
        data[TasksProperties.Done.num] = getBigDecimal(task.getDone());
        data[TasksProperties.Effort.num] = getBigDecimal(0D);
        data[TasksProperties.ToDo.num] = getBigDecimal(task.getToDo());
        final IListValueProperty status = task.getStatus();
        data[TasksProperties.Status.num] = status.getCurrentValue();
    }

    private static BigDecimal getBigDecimal(Double toDo) {
        return toDo == null ? null : BigDecimal.valueOf(toDo).setScale(2, BigDecimal.ROUND_HALF_UP);
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
        //data[TasksProperties.ID.getNum()] = task.getID();
        //data[TasksProperties.Parent.getNum()] = task.getParent().getName();
        task.setDetailEstimate(getDoubleValue(data[TasksProperties.DetailEstimeate.num]));
        //data[TasksProperties.Done.getNum()] = task.getDone();
        task.createEffort(getDoubleValue(data[TasksProperties.Effort.num].toString()), member);
        task.setToDo(getDoubleValue(data[TasksProperties.ToDo.num]));
        //final IListValueProperty status = task.getStatus();
        //data[TasksProperties.Status.getNum()] = status.getCurrentValue();
        task.getStatus().setCurrentValue(data[TasksProperties.Status.num] != null ? data[TasksProperties.Status.num].toString() : null);
    }

    private Double getDoubleValue(Object data) {
        return data != null ? Double.parseDouble(data.toString()) : null;
    }

    //TODO synchronized???
    public void setNewTaskValue(int task, TasksProperties property) {
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
        synchronized (tasksData) {
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
        }

        tasksData[task][property.num] = data;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

    public void removeProgressIndicator() {
        this.progressIndicator = null;
    }

    private boolean isConnectionValid() {
        boolean result = true;

        if (v1 != null) {
            try {
                v1.validate();
            } catch (ApplicationUnavailableException e) {
                result = false;
            }
        }
        else {
            result = false;
        }

        return result;
    }

    @NotNull
    public ProjectTreeNode getProjects() throws ConnectException {
        ProjectFilter filter = new ProjectFilter();
        filter.getState().add(BaseAssetFilter.State.Active);
        Collection<Project> projects;
        ProjectTreeNode treeProjects = new ProjectTreeNode("", null, 0, "");

        if (!isConnectionValid()) {
            connect();
        }

        synchronized (v1) {
            try {
                projects = v1.getProjects();
            } catch (Exception e) {
//                Messages.showMessageDialog(
//                        "Can't get projects list:\n" + e.getMessage(),
//                        "Error",
//                        Messages.getErrorIcon());
                LOG.error("Error on SDK level", e);
                throw new SDKException(e);
//                return treeProjects;
            } //catch (MetaException e) {
//                Messages.showMessageDialog(
//                        "Can't get projects list:\n" + e.getMessage(),
//                        "Error",
//                        Messages.getErrorIcon());
//                LOG.error("Error connection to VersionOne", e);
//                return treeProjects;
//            }

            Project mainProject = projects.iterator().next();

            treeProjects = new ProjectTreeNode(mainProject.getName(), null, 0, mainProject.getID().getToken());

            /*
            Collection<Project> projects = v1.getProjects().iterator().next().getChildProjects(filter, true);
            for(Project project : projects) {
                getAllChildren(project, projects);
            }
            */


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
        boolean result = true;


        synchronized (tasksData) {
            for (TasksProperties property : TasksProperties.values()) {

                // if property is not editable - next iteration
                if (!property.isEditable) {
                    continue;
                }

                if (tasksData[task][property.num] != null &&
                        defaultTaskData[task][property.num] != null) {
                    switch (property.type) {
                        case Number:
    //                        Double editedNumber = Double.parseDouble(tasksData[task][property.num].toString());
    //                        Double defaultNumber = Double.parseDouble(defaultTaskData[task][property.num].toString());
                            BigDecimal editedNumber = (BigDecimal) tasksData[task][property.num];
                            BigDecimal defaultNumber = (BigDecimal) defaultTaskData[task][property.num];
                            result = result && editedNumber.equals(defaultNumber);
                            break;
                        case StatusList:
                        case Text:
                            String editedData = tasksData[task][property.num].toString();
                            String defaultData = defaultTaskData[task][property.num].toString();
                            result = result && editedData.equals(defaultData);
                            break;
                    }
                } else if (tasksData[task][property.num] == null &&
                        defaultTaskData[task][property.num] == null) {
                    result = true;
                } else {
                    result = false;
                }

                if (!result) {
                    break;
                }
            }
        }

        return !result;
    }
}
