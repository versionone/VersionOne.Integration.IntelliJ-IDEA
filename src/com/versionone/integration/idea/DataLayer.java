package com.versionone.integration.idea;

import com.versionone.apiclient.V1Exception;
import com.versionone.common.sdk.IStatusCodes;
import com.versionone.common.sdk.TaskStatusCodes;
import com.versionone.om.ApiClientInternals;
import com.versionone.om.IListValueProperty;
import com.versionone.om.Member;
import com.versionone.om.Project;
import com.versionone.om.Task;
import com.versionone.om.V1Instance;
import com.versionone.om.filters.TaskFilter;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;

/**
 * Requests, cache, get change requests and store data from VersionOne server.
 */
public final class DataLayer {
    private static final Logger LOG = Logger.getLogger(DataLayer.class);

    private static DataLayer instance;

/*
    public String v1Path = "http://jsdksrv01/VersionOne/";
    public String user = "admin";
    public String passwd = "admin";
    public String projectName = "V1EclipseTestPrj";
*/
    private final WorkspaceSettings cfg;

    private V1Instance v1;
    private Member member;
    private IStatusCodes statusList;
    private boolean trackEffort;
    private Object[][] tasksData;

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

    private static final String[] TASK_ATTRIBUTES = "Name,Description,Category,Customer,DetailEstimate,Estimate,LastVersion,Number,Owners,Parent,Reference,Scope,Source,Status,Timebox,ToDo,Actuals.Value.@Sum".split(",");

    public void refresh() {
        LOG.info("DataLayer.refresh()");

        final Project project = v1.get().projectByName(cfg.projectName);
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
//        filter.state.add(BaseAssetFilter.State.Active);   //TODO Make cange in SDK
        filter.owners.add(member);
        Collection<Task> tasks = v1.get().tasks(filter);
        tasksData = new Object[tasks.size()][TasksProperties.COUNT];
        int i = 0;
        for (Task task : tasks) {
            if (task.getParent().getIteration().isActive()) {
                if (task.isActive()) {
                    setTaskData(tasksData[i++], task);
                }
            }
        }
        tasksData = Arrays.copyOf(tasksData, i);

        if (LOG.isInfoEnabled()) {
            wr();
        }
    }

    /**
     * temp
     */
    private void wr() {
        for (int i = 0; i < tasksData.length; i++) {
            Object[] objects = tasksData[i];

        }
        for (Object task : TasksProperties.values()) {
                System.out.println(task);
        }
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

    public int getTasksCount() {
        return tasksData.length;
    }

    public Object getTaskPropertyValue(int task, TasksProperties property) {
        return tasksData[task][property.getNum()];
    }

    public String[] getAllStatuses() {
        return statusList.getDisplayValues();
    }

    public static DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer(new WorkspaceSettings());
        }
        return instance;
    }

    public void setTaskPropertyValue(int task, TasksProperties property, Object value) {
        tasksData[task][property.getNum()] = value;
    }
}
