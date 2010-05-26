package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;

public class ToolComponent implements ToolService{
    private final TasksComponent tc;
    private final DetailsComponent dc;
    private final IDataLayer dataLayer;

    public ToolComponent(TasksComponent tc, DetailsComponent dc) {
        this.tc = tc;
        this.dc = dc;
        dataLayer = tc.getDataLayer();
    }

    public void registerTool() {
        tc.registerTool();
        dc.registerTool();
    }

    public void unregisterTool() {
        tc.unregisterTool();
        dc.unregisterTool();
    }

    public void update() {
        tc.refresh();
        tc.update();
        dc.update();
    }

    public IDataLayer getDataLayer() {
        return dataLayer;
    }
}
