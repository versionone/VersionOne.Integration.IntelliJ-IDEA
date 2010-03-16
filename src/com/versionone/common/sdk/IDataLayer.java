package com.versionone.common.sdk;

import java.util.List;

public interface IDataLayer {

    boolean checkConnection(String url, String user, String pass, boolean integratedAuth);
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
}
