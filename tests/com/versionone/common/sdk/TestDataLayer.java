package com.versionone.common.sdk;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.versionone.apiclient.Asset;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.PropertyValues;

public class TestDataLayer extends ApiDataLayer {

    public static TestDataLayer getInstance() {
        if (!(ApiDataLayer.instance instanceof TestDataLayer)) {
            ApiDataLayer.instance = new TestDataLayer();
        }
        return (TestDataLayer) ApiDataLayer.instance;
    }

    public boolean isEffortTracking;
    public List<PrimaryWorkitem> workitemTree = new LinkedList<PrimaryWorkitem>();

    private Map<String, PropertyValues> listProperties = new HashMap<String, PropertyValues>();

    @Override
    public void removeWorkitem(Workitem item) {
    }

    @Override
    public void addProperty(String attr, EntityType type, boolean isList) {
    }

    public void setListProperty(String attr, EntityType type, PropertyValues values) {
        listProperties.put(type + attr, values);
    }

    @Override
    public boolean checkConnection(String url, String user, String pass, boolean auth) {
        return true;
    }

    @Override
    public void commitChanges() throws DataLayerException {
    }

    @Override
    public void connect(String path, String userName, String password, boolean integrated) throws DataLayerException {
    }

    @Override
    public String getCurrentMemberToken() {
        return "Member:20";
    }

    @Override
    public Project getCurrentProject() {
        return null;
    }

    @Override
    public String getCurrentProjectId() {
        return getCurrentProject().getId();
    }

    @Override
    public PropertyValues getListPropertyValues(EntityType type, String propertyName) {
        final PropertyValues res = listProperties.get(type + propertyName);
        return res == null ? new PropertyValuesMock("") : res;
    }

    @Override
    public List<Project> getProjectTree() throws DataLayerException {
        return Arrays.asList(getCurrentProject());
    }

    @Override
    public List<PrimaryWorkitem> getWorkitemTree() throws DataLayerException {
        return workitemTree;
    }

    @Override
    public boolean isDisplayed(Asset asset) {
        return false;
    }

    @Override
    public boolean isTrackEffortEnabled() {
        return isEffortTracking;
    }

    @Override
    public String localizerResolve(String key) {
        if (key.startsWith("ColumnTitle'")) {
            if (key.equals("ColumnTitle'DetailEstimate")) {
                return "Detail Estimate";
            } else if (key.equals("ColumnTitle'ToDo")) {
                return "To Do";
            }
            return key.substring("ColumnTitle'".length());
        }
        return key;
    }

    @Override
    public void reconnect() throws DataLayerException {
    }

    @Override
    public void setCurrentProject(Project value) {
    }

    @Override
    public void setCurrentProjectId(String value) {
    }

    @Override
    public String updateCurrentProjectId() {
        return getCurrentProjectId();
    }

}
