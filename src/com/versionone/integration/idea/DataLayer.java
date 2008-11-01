package com.versionone.integration.idea;

import com.versionone.om.V1Instance;
import com.versionone.om.Project;
import com.versionone.om.Story;
import com.versionone.om.IListValueProperty;
import com.versionone.om.Task;

import java.util.Collection;
import java.util.Iterator;

/**
 * Requests, cache, get change requests and store data from VersionOne server.
 */
public final class DataLayer {
    public String v1Path = "http://jsdksrv01/VersionOne/";
    public String user = "admin";
    public String passwd = "admin";
    public String projectName = "V1EclipseTestPrj";

    private V1Instance v1;
    private String[] statusList;

    private Object[][] data;
    private static DataLayer instance;

    public DataLayer() {
        v1 = new V1Instance(v1Path,user,passwd);
        refresh();
    }

    private void refresh() {
        Project prj = v1.get().projectByName(projectName);
        if (prj == null){
            return;
        }
        Collection<Task> tasks = prj.getTasks(null);
        data  = new Object[tasks.size()][ColunmnsNames.COUNT];
        int i=0;
        for (Task task : tasks) {
            setTaskData(data[i++], task);
        }

/*
        data  = new Object[][] {
            {"Title", "ID", "Parent", "Detail Estimeate", "Done", "Effort", "To Do", "Status"},
            {"Alison", "Huml", "Rowing", 3, true, 1, 1, "Done"},
            {"Kathy", "Walrath", "Knitting", 2, false, 1, 1, "In Progress"},
            {"Sharon", "Zakhour", "Speed reading", 20, true, 1, 1, "In Progress"},
            {"Philip", "Milne", "Pool", 10, false, 1, 1, "Done"}
        };
*/

        statusList = new String[] {"", "In Progress", "Done", "My :)"};
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
        return statusList;
    }

    public static DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer();
        }
        return instance;
    }
}
