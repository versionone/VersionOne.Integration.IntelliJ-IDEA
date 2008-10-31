package com.versionone.integration.idea;

/**
 * Requests, cache, get change requests and store data from VersionOne server.
 */
public final class DataLayer {

    public Object[][] getMainData() {
        Object[][] data = {
            {"Title", "ID",
             "Parent", "Detail Estimeate", "Done", "Effort", "To Do", "Status"},
            {"Alison", "Huml",
             "Rowing", 3, true, 1, 1, "2"},
            {"Kathy", "Walrath",
             "Knitting", 2, false, 1, 1, "3"},
            {"Sharon", "Zakhour",
             "Speed reading", 20, true, 1, 1, "1"},
            {"Philip", "Milne",
             "Pool", 10, false, 1, 1, "=="}
        };

        return data;
    }

    public String[] getAllStatuses() {
        return new String[]{"", "In Progress", "Done"};
    }
}
