/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.tests;

import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.TasksProperties;
import com.versionone.om.Project;
import com.versionone.common.sdk.ProjectTreeNode;
import com.versionone.common.sdk.IProjectTreeNode;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.util.Collection;
import java.util.List;

public class DataLayerTest {
    private DataLayer data;

    @Before
    public void before() {
        data = DataLayer.getInstance();
    }

    @Test
    @Ignore
    public void testGetMainData() {
        int list = data.getTasksCount();
        for (int i = 0; i < list; i++) {
            for (TasksProperties property : TasksProperties.values()) {
                System.out.println(data.getTaskPropertyValue(i,property));
            }
        }
    }

    @Test
    public void testGetProjects() {
        IProjectTreeNode projects = data.getProjects();

        //for(Project project : projects) {

        System.out.println(projects.getToken() + " " + projects.getName());
        displayAllProjects(projects.getChildren(), 0);

        //}
    }

    private void displayAllProjects(IProjectTreeNode[] projectTreeNodes, int pos){
        for(IProjectTreeNode project : projectTreeNodes) {
            System.out.print(pos);
            for (int i=0; i<pos*2; i++) {
                System.out.print("-");
            }
            System.out.println(project.getToken() + " " + project.getName());
            if (project.hasChildren()) {
                displayAllProjects(project.getChildren(), pos + 1);
            }


        }

    }
}        
