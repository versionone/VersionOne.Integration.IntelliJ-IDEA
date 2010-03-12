/*(c) Copyright 2010, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.EntityType;

/**
 * Data configuration
 */
public class Configuration {

    public void fill() {
        addAttribute(EntityType.Task);
        addAttribute(EntityType.Defect);
        addAttribute(EntityType.Scope);
        addAttribute(EntityType.Story);
        addAttribute(EntityType.Test);
    }


    private void addAttribute(EntityType type) {
        ApiDataLayer.getInstance().addProperty("Name", type, false);
        ApiDataLayer.getInstance().addProperty("Number", type, false);
        ApiDataLayer.getInstance().addProperty("Parent.Name", type, false);
        ApiDataLayer.getInstance().addProperty("DetailEstimate", type, false);
        ApiDataLayer.getInstance().addProperty("Actuals.Value.@Sum", type, false);
        ApiDataLayer.getInstance().addProperty("ToDo", type, false);
        ApiDataLayer.getInstance().addProperty("Status", type, true);
    }
}
