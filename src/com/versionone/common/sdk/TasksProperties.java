/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.sdk;

import com.versionone.Oid;

import java.util.Collection;
import java.util.LinkedList;

public enum TasksProperties {

    TITLE("Title", Type.Text, true, "Name"),
    ID("ID", Type.Text, false, "Number"),
    PARENT("Story", Type.Text, false, "Parent.Name"),
    DETAIL_ESTIMATE("Detailed Estimate", Type.Number, true, "DetailEstimate"),
    DONE("Done", Type.Number, false, "Actuals.Value.@Sum"),
    EFFORT("Effort", Type.Number, true),
    TO_DO("Todo", Type.Number, true, "ToDo"),
    /**
     * @deprecated
     */
    STATUS_NAME("Status", Type.StatusList, true, "Status"),
    STATUS("Status", Type.List, true, "Status"),
    DESCRIPTION("Description", Type.RichText, true, "Description"),
    OWNER("Owner", Type.Text, false, "Owners.Nickname"),
    PROJECT("Project", Type.Text, false, "Scope.Name"),
    REFERENCE("Reference", Type.Text, true, "Reference"),
    SOURCE("Source", Type.List, true, "Source"),
    SPRINT("Sprint", Type.Text, false, "Timebox.Name"),
    TYPE("Type", Type.List, true, "Category"),
    BUILD("Build", Type.Text, false, "LastVersion");

    public final String columnName;
    public final Type type;
    public final boolean isEditable;
    public final String propertyName;

    TasksProperties(String name, Type type, boolean editable) {
        this.columnName = name;
        this.type = type;
        isEditable = editable;
        propertyName = null;
    }

    TasksProperties(String columnName, Type type, boolean editable, String propertyName) {
        this.columnName = columnName;
        this.type = type;
        isEditable = editable;
        this.propertyName = propertyName;
    }

    static Collection<String> getAllAttributes() {
        final TasksProperties[] v = TasksProperties.values();
        final LinkedList<String> res = new LinkedList<String>();
        for (TasksProperties property : v) {
            final String pName = property.propertyName;
            if (pName != null)
                res.add(pName);
        }
        return res;
    }

    static boolean isEqual(Object oldProp, Object newProp) {
        if (oldProp == null || newProp == null) {
            return oldProp != newProp;
        }
        if (oldProp instanceof Double) {
            return Math.abs((Double) oldProp - Double.parseDouble(newProp.toString())) < 0.005;
        }
        if (oldProp instanceof Oid) {
            return oldProp.toString().equals(newProp.toString());
        }
        return oldProp.equals(newProp);
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
