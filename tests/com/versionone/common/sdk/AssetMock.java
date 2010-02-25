package com.versionone.common.sdk;

import java.util.LinkedList;
import java.util.List;

import com.versionone.apiclient.Asset;
import com.versionone.apiclient.IAssetType;

public class AssetMock extends Asset {

    public List<Asset> children = new LinkedList<Asset>();

    public AssetMock(IAssetType assetType) {
        super(assetType);
    }

    @Override
    public List<Asset> getChildren() {
        return children;
    }
}