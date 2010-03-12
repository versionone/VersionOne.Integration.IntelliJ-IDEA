package com.versionone.common.sdk;

import java.util.List;

public interface IDataLayer {

    static final IDataLayer INSTANCE = ApiDataLayer.getInstance();

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
