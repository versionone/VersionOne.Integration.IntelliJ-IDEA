package com.versionone.common.sdk;

import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IOperation;
import com.versionone.apiclient.MetaException;

public class AssetTypeMock implements IAssetType {

    public String token;

    public AssetTypeMock(String token) {
        this.token = token;
    }

    public IAttributeDefinition getAttributeDefinition(String name) throws MetaException {
        // TODO Auto-generated method stub
        return null;
    }

    public IAssetType getBase() throws MetaException {
        // TODO Auto-generated method stub
        return null;
    }

    public IAttributeDefinition getDefaultOrderBy() throws MetaException {
        // TODO Auto-generated method stub
        return null;
    }

    public IAttributeDefinition getDescriptionAttribute() throws MetaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDisplayName() {
        // TODO Auto-generated method stub
        return null;
    }

    public IAttributeDefinition getNameAttribute() throws MetaException {
        // TODO Auto-generated method stub
        return null;
    }

    public IOperation getOperation(String name) throws MetaException {
        // TODO Auto-generated method stub
        return null;
    }

    public IAttributeDefinition getShortNameAttribute() throws MetaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getToken() {
        return token;
    }

    public boolean isA(IAssetType targetType) throws MetaException {
        // TODO Auto-generated method stub
        return false;
    }

}
