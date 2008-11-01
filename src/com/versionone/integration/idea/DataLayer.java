package com.versionone.integration.idea;

/**
 * Requests, cache, get change requests and store data from VersionOne server.
 */
public final class DataLayer {

    private Object[][] data = {
            {"Title", "ID",
             "Parent", "Detail Estimeate", "Done", "Effort", "To Do", "Status"},
            {"Alison", "Huml",
             "Rowing", 3, true, 1, 1, "Done"},
            {"Kathy", "Walrath",
             "Knitting", 2, false, 1, 1, "In Progress"},
            {"Sharon", "Zakhour",
             "Speed reading", 20, true, 1, 1, "In Progress"},
            {"Philip", "Milne",
             "Pool", 10, false, 1, 1, "Done"}
        };
    private static DataLayer instance;

    public Object[][] getMainData() {
        return data;
    }

    public Object getValue(int col, int row) {
        return data[row][col];
    }

    public String[] getAllStatuses() {
        return new String[]{"", "In Progress", "Done"};
    }

    public static DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer();
        }
        return instance;
    }
}
