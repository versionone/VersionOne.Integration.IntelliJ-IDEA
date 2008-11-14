/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

public enum TasksProperties {

    Title(0, "Task", Type.Text, true),
    ID(1, "ID", Type.Text, true),
    Parent(2, "Story", Type.Text, false),
    DetailEstimeate(3, "Detailed Estimate", Type.Number, true),
    Done(4, "Done", Type.Number, false),
    Effort(5, "Effort", Type.Number, true),
    ToDo(6, "Todo", Type.Number, true),
    Status(7, "Status", Type.StatusList, true);

    final int num;
    final String columnName;
    final Type type;
    final boolean isEditable;

    TasksProperties(int num, String columnName, Type type, boolean editable) {
        this.num = num;
        this.columnName = columnName;
        this.type = type;
        isEditable = editable;
    }

    public static enum Type {
        Number, Text, StatusList;

        public final Class<?> columnClass;

        Type() {
            columnClass = Object.class;
        }

        Type(Class<?> aClass) {
            columnClass = aClass;
        }
    }
}
