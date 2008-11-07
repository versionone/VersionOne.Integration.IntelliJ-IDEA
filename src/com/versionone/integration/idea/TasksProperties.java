/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

public enum TasksProperties {

    Title(0), ID(1), Parent(2), DetailEstimeate(3), Done(4), Effort(5), ToDo(6), Status(7,"Status","list",true);

    public static final int COUNT = 8;

    int num;
    String columnName;
    String type;        // TODO make it enum
    boolean isEditable;

    TasksProperties(int num, String columnName, String type, boolean editable) {
        this.num = num;
        this.columnName = columnName;
        this.type = type;
        isEditable = editable;
    }

    TasksProperties(int i) {
        this(i, "not implemented!!!", "", false);
    }

    public int getNum() {
        return num;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getType() {
        return type;
    }

    public boolean isEditable() {
        return isEditable;
    }
}
