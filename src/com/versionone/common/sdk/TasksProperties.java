/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.sdk;

import java.util.Collection;
import java.util.LinkedList;

public enum TasksProperties {

    TITLE(0, "Title", Type.Text, true, "Name"),
    ID(1, "ID", Type.Text, false, "Number"),
    PARENT(2, "Story", Type.Text, false, "Parent.Name"),
    DETAIL_ESTIMATE(3, "Detailed Estimate", Type.Number, true, "DetailEstimate"),
    DONE(4, "Done", Type.Number, false, "Actuals.Value.@Sum"),
    EFFORT(5, "Effort", Type.Number, true),
    TO_DO(6, "Todo", Type.Number, true, "ToDo"),
    /**
     * @deprecated
     */
    STATUS_NAME(7, "Status", Type.StatusList, true, "Status"),
    STATUS(15, "Status", Type.List, true, "Status"),
    DESCRIPTION(8, "Description", Type.RichText, true, "Description"),
    OWNER(9, "Owner", Type.Text, false, "Owners.Nickname"),
    PROJECT(10, "Project", Type.Text, false, "Scope.Name"),
    REFERENCE(11, "Reference", Type.Text, true, "Reference"),
    SOURCE(12, "Source", Type.List, true, "Source"),
    SPRINT(13, "Sprint", Type.Text, false, "Timebox.Name"),
    TYPE(14, "Type", Type.List, true, "Category"),
    BUILD(16, "Build", Type.Text, false, "LastVersion");

    final int num;
    public final String columnName;
    public final Type type;
    public final boolean isEditable;
    public final String propertyName;

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
            final String pName = property.propertyName;
            if (pName != null)
                res.add(pName);
        }
        return res;
    }

    public static enum Type {
        Number, RichText, Text, StatusList, List;

        public final Class<?> columnClass;

        Type() {
            columnClass = Object.class;
        }

        Type(Class<?> aClass) {
            columnClass = aClass;
        }
    }
}
