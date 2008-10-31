package com.versionone.integration.idea;

/**
 * 
 */
public class ColumnData {
    private String title;
    private String type; // TODO make it enum
    private boolean isEditable;

    public ColumnData(String title, String type, boolean editable) {
        this.title = title;
        this.type = type;
        isEditable = editable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public static String[] getTitleNames(ColumnData[] columnData) {
        String[] titles = new String[columnData.length];
        for (int i=0; i<columnData.length; i++) {
            titles[i] = columnData[i].getTitle();
        }

        return titles;
    }
}
