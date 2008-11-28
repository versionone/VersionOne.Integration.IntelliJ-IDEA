/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.sdk;

import com.versionone.Oid;
import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IServices;
import com.versionone.apiclient.V1Exception;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

public enum TasksProperties {

    TITLE("Title", Type.TEXT, true, "Name"),
    ID("ID", Type.TEXT, false, "Number"),
    PARENT("Story", Type.TEXT, false, "Parent.Name"),
    DETAIL_ESTIMATE("Detailed Estimate", Type.NUMBER, true, "DetailEstimate"),
    DONE("Done", Type.NUMBER, false, "Actuals.Value.@Sum"),
    EFFORT("Effort", Type.NUMBER, true),
    TO_DO("Todo", Type.NUMBER, true, "ToDo"),
    /**
     * @deprecated
     */
    STATUS_NAME("Status", Type.STATUS_LIST, true, "Status"),
    STATUS("Status", Type.LIST, true, "Status"),
    DESCRIPTION("Description", Type.RICH_TEXT, true, "Description"),
    OWNER("Owner", Type.TEXT, false, "Owners.Nickname"),
    PROJECT("Project", Type.TEXT, false, "Scope.Name"),
    REFERENCE("Reference", Type.TEXT, true, "Reference"),
    SOURCE("Source", Type.LIST, true, "Source"),
    SPRINT("Sprint", Type.TEXT, false, "Timebox.Name"),
    TYPE("Type", Type.LIST, true, "Category"),
    BUILD("Build", Type.TEXT, false, "LastVersion");

    public final String columnName;
    public final Type type;
    public final boolean isEditable;
    public final String propertyName;
    private String assetType;
    private ListTypeValues listValues;


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
        if (type == Type.LIST) {
            assetType = "Task" + propertyName;
        }
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

    /*
            statusList = new ListTypeValues(metaModel, services, "TaskStatus");
            typesList = new ListTypeValues(metaModel, services, "TaskCategory");
            sourcesList = new ListTypeValues(metaModel, services, "TaskSource");
*/

    public static void reloadListValues(IMetaModel model, IServices services) throws V1Exception {
        for (TasksProperties property : TasksProperties.values()) {
            property.reloadValues(model, services);
        }
    }

    private void reloadValues(IMetaModel metaModel, IServices services) throws V1Exception {
        if (assetType != null) {
            listValues = new ListTypeValues(metaModel, services, assetType);
        }
    }

    public String getValueName(Oid oid) {
        return listValues.getName(oid);
    }

    public Vector<String> getListValues() {
        if (listValues != null) {
            return listValues.getAllNames();
        }
        return new Vector<String>(0);
    }

    public Object getValueOid(String value) {
        if (listValues != null) {
            return listValues.getOid(value);
        }
        return value;
    }

    public static enum Type {
        NUMBER, RICH_TEXT, TEXT, STATUS_LIST, LIST;
    }
}
