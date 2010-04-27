package com.versionone.common.sdk;

import java.util.List;
import java.util.Map;

public interface IDataLayer {

    boolean verifyConnection(String url, String user, String pass, boolean integratedAuth);
    void connect(String path, String userName, String password, boolean integrated) throws DataLayerException;

    /**
     * Reconnect with settings, used in last Connect() call.
     *
     * @throws DataLayerException
     */
    void reconnect() throws DataLayerException;
    List<Project> getProjectTree() throws DataLayerException;
    List<PrimaryWorkitem> getWorkitemTree() throws DataLayerException;
    boolean hasChanges();
    void commitChanges() throws DataLayerException, ValidatorException;
    boolean isTrackEffortEnabled();
    void addProperty(String name, EntityType type, boolean isList);
    String localizerResolve(String key);

    void setCurrentProjectId(String projectId);
    String getCurrentProjectId();
    void setShowAllTasks(boolean showAllTasks);

    PropertyValues getListPropertyValues(EntityType type, String propertyName);

    boolean isConnected();
    PrimaryWorkitem createNewPrimaryWorkitem(EntityType type) throws DataLayerException;
    SecondaryWorkitem createNewSecondaryWorkitem(EntityType type, PrimaryWorkitem parent) throws DataLayerException;
}
