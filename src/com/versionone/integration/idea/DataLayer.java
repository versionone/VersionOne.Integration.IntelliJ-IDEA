package com.versionone.integration.idea;

import com.versionone.apiclient.V1Exception;
import com.versionone.common.sdk.IStatusCodes;
import com.versionone.common.sdk.TaskStatusCodes;
import com.versionone.om.ApiClientInternals;
import com.versionone.om.IListValueProperty;
import com.versionone.om.Project;
import com.versionone.om.Task;
import com.versionone.om.V1Instance;

import java.util.Collection;

/**
 * Requests, cache, get change requests and store data from VersionOne server.
 */
public final class DataLayer {
    private static DataLayer instance;

    public String v1Path = "http://jsdksrv01/VersionOne/";
    public String user = "admin";
    public String passwd = "admin";
    public String projectName = "V1EclipseTestPrj";

    private V1Instance v1;
    private IStatusCodes statusList;
    private boolean trackEffort;
    private Object[][] data;

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

    public void refresh() {
        System.out.println("DataLayer.refresh()");
        Project prj = v1.get().projectByName(projectName);
        if (prj == null) {
            return;
        }
        Collection<Task> tasks = prj.getTasks(null);
        data = new Object[tasks.size()][ColunmnsNames.COUNT];
        int i = 0;
        for (Task task : tasks) {
            setTaskData(data[i++], task);
        }

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
        return data;
    }

    public Object getValue(int col, int row) {
        return data[row][col];
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
