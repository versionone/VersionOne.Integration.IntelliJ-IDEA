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

    public String v1Path = "http://jsdksrv01/VersionOne/";
    public String user = "admin";
    public String passwd = "admin";
    public String projectName = "V1EclipseTestPrj";

    private V1Instance v1;
    private IStatusCodes statusList;
    private boolean trackEffort;
    private Object[][] tasksData;

    private DataLayer() {
        try {
            v1 = new V1Instance(v1Path, user, passwd);
            final ApiClientInternals apiClient = v1.getApiClient();
            statusList = new TaskStatusCodes(apiClient.getMetaModel(), apiClient.getServices());
            trackEffort = v1.getConfiguration().effortTrackingEnabled;
        } catch (V1Exception e) {
            e.printStackTrace();
        }
        refresh();
    }

    private static final String[] TASK_ATTRIBUTES = "Name,Description,Category,Customer,DetailEstimate,Estimate,LastVersion,Number,Owners,Parent,Reference,Scope,Source,Status,Timebox,ToDo,Actuals.Value.@Sum".split(",");

    public void refresh() {
        LOG.info("DataLayer.refresh()");

        final Project project = v1.get().projectByName(projectName);
        if (project == null) {
            LOG.error("There is no project: " + projectName);
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
        final Member member = v1.get().memberByUserName(user);//TODO cache
        filter.owners.add(member);
        Collection<Task> tasks = v1.get().tasks(filter);
        tasksData = new Object[tasks.size()][ColunmnsNames.COUNT];
        int i = 0;
        for (Task task : tasks) {
            if (task.getParent().getIteration().isActive()) {//TODO it's a workaround
                if (task.isActive()) {//TODO it's a workaround
                    setTaskData(tasksData[i++], task);
                }
            }
        }
        tasksData = Arrays.copyOf(tasksData, i);

        wr();
    }

    /**
     * temp
     */
    private void wr() {
        Object[][] x = getMainData();
        for (Object[] objects : x) {
            for (Object o : objects) {
                System.out.print(o + "|");
            }
            System.out.print("\n");
        }
    }


    private static void setTaskData(Object[] data, Task task) {
        data[ColunmnsNames.Title.getNum()] = task.getName();
        data[ColunmnsNames.ID.getNum()] = task.getID();
        data[ColunmnsNames.Parent.getNum()] = task.getParent().getName();
        data[ColunmnsNames.DetailEstimeate.getNum()] = task.getDetailEstimate();
        data[ColunmnsNames.Done.getNum()] = task.getDone();
        data[ColunmnsNames.Effort.getNum()] = 0;
        data[ColunmnsNames.ToDo.getNum()] = task.getToDo();
        final IListValueProperty status = task.getStatus();
        data[ColunmnsNames.Status.getNum()] = status.getCurrentValue();
    }

    public Object[][] getMainData() {
        return tasksData;
    }

    public Object getValue(int col, int row) {
        return tasksData[row][col];
    }

    public String[] getAllStatuses() {
        return statusList.getDisplayValues();
    }

    public static DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer();
        }
        return instance;
    }
}
