package com.versionone.common.sdk;

import java.util.List;

public interface IDataLayer {

    static final IDataLayer INSTANCE = ApiDataLayer.getInstance();

    List<Project> getProjectTree() throws DataLayerException;
    List<PrimaryWorkitem> getWorkitemTree() throws DataLayerException;
    boolean hasChanges();
    void commitChanges() throws DataLayerException, ValidatorException;
}
