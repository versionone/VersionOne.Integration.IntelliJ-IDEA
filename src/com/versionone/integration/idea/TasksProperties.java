/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

public enum TasksProperties {

    Title(0, "Task", Type.String, true),
    ID(1, "ID", Type.String, false),
    Parent(2, "Story", Type.String, false),
    DetailEstimeate(3, "Detailed Estimate", Type.Number, true),
    Done(4, "Done", Type.Number, false),
    Effort(5, "Effort", Type.Number, true),
    ToDo(6, "Todo", Type.Number, true),
    Status(7, "Status", Type.StatusList, true);

    public static final int COUNT = 8;

    int num;
    String columnName;
    Type type;        // TODO make it enum
    boolean isEditable;

    TasksProperties(int num, String columnName, Type type, boolean editable) {
        this.num = num;
        this.columnName = columnName;
        this.type = type;
        isEditable = editable;
    }

    public int getNum() {
        return num;
    }

    public String getColumnName() {
        return columnName;
    }

    public Type getType() {
        return type;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public static enum Type {
        Number, String, StatusList
    }
}
