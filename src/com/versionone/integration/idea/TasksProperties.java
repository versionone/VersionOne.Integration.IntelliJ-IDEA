/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import java.util.Collection;
import java.util.LinkedList;

public enum TasksProperties {

    TITLE(0, "Task", Type.Text, true, "Name"),
    ID(1, "ID", Type.Text, false, "Number"),
    PARENT(2, "Story", Type.Text, false, "Parent.Name"),
    DETAIL_ESTIMATE(3, "Detailed Estimate", Type.Number, true, "DetailEstimate"),
    DONE(4, "Done", Type.Number, false, "Actuals.Value.@Sum"),
    EFFORT(5, "Effort", Type.Number, true),
    TO_DO(6, "Todo", Type.Number, true, "ToDo"),
    STATUS(7, "Status", Type.StatusList, true, "Status");

    final int num;
    final String columnName;
    final Type type;
    final boolean isEditable;
    final private String propertyName;

    TasksProperties(int num, String name, Type type, boolean editable) {
        this.num = num;
        this.columnName = name;
        this.type = type;
        isEditable = editable;
        propertyName = null;
    }

    TasksProperties(int num, String columnName, Type type, boolean editable, String propertyName) {
        this.num = num;
        this.columnName = columnName;
        this.type = type;
        isEditable = editable;
        this.propertyName = propertyName;
    }

    public static Collection<String> getAllAttributes() {
        final TasksProperties[] v = TasksProperties.values();
        final LinkedList<String> res = new LinkedList<String>();
        for (TasksProperties property : v) {
            final String pName = property.getName();
            if (pName != null)
                res.add(pName);
        }
        return res;
    }

    public String getName() {
        return propertyName;
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
