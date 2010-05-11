///*(c) Copyright 2010, VersionOne, Inc. All rights reserved. (c)*/
//package com.versionone.integration.idea;
//
//import com.versionone.common.sdk.ApiDataLayer;
//import com.versionone.common.sdk.EntityType;
//
///**
// * Data configuration
// */
//class Configuration {
//
//    public void fill() {
//        addAttribute(EntityType.Task);
//        addAttribute(EntityType.Defect);
//        addAttribute(EntityType.Scope);
//        addStoryAttribute(EntityType.Story);
//        addAttribute(EntityType.Test);
//    }
//
//
//    private void addAttribute(EntityType type) {
//        ApiDataLayer.getInstance().addProperty("Name", type, false);
//        ApiDataLayer.getInstance().addProperty("Number", type, false);
//        ApiDataLayer.getInstance().addProperty("Parent.Name", type, false);
//        ApiDataLayer.getInstance().addProperty("DetailEstimate", type, false);
//        ApiDataLayer.getInstance().addProperty("Actuals.Value.@Sum", type, false);
//        ApiDataLayer.getInstance().addProperty("ToDo", type, false);
//        ApiDataLayer.getInstance().addProperty("Status", type, true);
//    }
//
//
//    private void addStoryAttribute(EntityType type) {
//        ApiDataLayer.getInstance().addProperty("Name", type, false);
//        ApiDataLayer.getInstance().addProperty("Number", type, false);
//        ApiDataLayer.getInstance().addProperty("Parent.Name", type, false);
//        ApiDataLayer.getInstance().addProperty("DetailEstimate", type, false);
//        ApiDataLayer.getInstance().addProperty("Actuals.Value.@Sum", type, false);
//        ApiDataLayer.getInstance().addProperty("ToDo", type, false);
//        ApiDataLayer.getInstance().addProperty("Status", type, true);
//        ApiDataLayer.getInstance().addProperty("LastVersion", type, false);
//        ApiDataLayer.getInstance().addProperty("Description", type, false);
//        ApiDataLayer.getInstance().addProperty("Owners.Nickname", type, false);
//        ApiDataLayer.getInstance().addProperty("Reference", type, false);
//        ApiDataLayer.getInstance().addProperty("Source", type, false);
//        ApiDataLayer.getInstance().addProperty("Category", type, false);
//        ApiDataLayer.getInstance().addProperty("Timebox.Name", type, false);
//
//
//
//
//
//    }
//}
