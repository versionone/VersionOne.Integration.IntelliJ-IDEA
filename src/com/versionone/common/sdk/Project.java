package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.List;

import com.versionone.apiclient.Asset;

public class Project extends Entity {

    public static final String OWNER_PROPERTY = "Owner";

    public final Project parent;
    /** List of child Projects. */
    public final List<Project> children;

    Project(ApiDataLayer dataLayer, Asset asset) {
        this(dataLayer, asset, null);
    }

    Project(ApiDataLayer dataLayer, Asset asset, Project parent) {
        super(dataLayer, asset);
        this.parent = parent;
        children = new ArrayList<Project>(asset.getChildren().size());
        for (Asset childAsset : asset.getChildren()) {
            children.add(new Project(dataLayer, childAsset, this));
        }
    }

    public static String getNameById(List<Project> projects, String id) {
        String name = "";
        if (projects == null || projects.size() == 0) {
            return name;
        }
        for(Project project : projects) {
            if (project.getId().equals(id)) {
                return project.getProperty(Entity.NAME_PROPERTY).toString();
            }
            return getNameById(project.children, id);
        }

        return name;
    }
}
