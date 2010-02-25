package com.versionone.common.sdk;

import com.versionone.apiclient.Asset;

public class SecondaryWorkitem extends Workitem {

    public final PrimaryWorkitem parent;

    SecondaryWorkitem(ApiDataLayer dataLayer, Asset asset, PrimaryWorkitem parent) {
        super(dataLayer, asset);
        this.parent = parent;
    }
}
