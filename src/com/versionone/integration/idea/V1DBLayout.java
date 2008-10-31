package com.versionone.integration.idea;

/**
 * 
 */
public class V1DBLayout {


    public Object[][] getMainData() {
        Object[][] data = {
            {"Title", "ID",
             "Parent", "Detail Estimeate", "Done", "Effort", "To Do", "Status"},
            {"Alison", "Huml",
             "Rowing", new Integer(3), new Boolean(true), 1, 1, "2"},
            {"Kathy", "Walrath",
             "Knitting", new Integer(2), new Boolean(false), 1, 1, "3"},
            {"Sharon", "Zakhour",
             "Speed reading", new Integer(20), new Boolean(true), 1, 1, "1"},
            {"Philip", "Milne",
             "Pool", new Integer(10), new Boolean(false), 1, 1, "=="}
        };

        return data;
    }

    public String[] getAllStatuses() {
        String[] statuses = {"", "In Progress", "Done"};

        return statuses;
    }
}
