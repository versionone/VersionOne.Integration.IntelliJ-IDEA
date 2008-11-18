/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

public enum TasksProperties {

    TITLE(0, "Task", Type.Text, true),
    ID(1, "ID", Type.Text, true),
    PARENT(2, "Story", Type.Text, false),
    DETAIL_ESTIMATE(3, "Detailed Estimate", Type.Number, true),
    DONE(4, "Done", Type.Number, false),
    EFFORT(5, "Effort", Type.Number, true),
    TO_DO(6, "Todo", Type.Number, true),
    STATUS(7, "Status", Type.StatusList, true);

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
