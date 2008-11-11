/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.tests;

import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.TasksProperties;
import com.versionone.integration.idea.ProjectTreeNode;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;

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
    @Ignore
    public void testGetProjects() {
        com.versionone.integration.idea.ProjectTreeNode projects = data.getProjects();

        //for(Project project : projects) {

        System.out.println(projects.toString() + " --- " +projects.getToken() + " child:" + projects.getChildCount());
        displayAllProjects(projects.children, 0);

        //}
    }

    private void displayAllProjects(ArrayList<ProjectTreeNode> projectTreeNodes, int pos){
        for(ProjectTreeNode project : projectTreeNodes) {
            System.out.print(pos);
            for (int i=0; i<pos*2; i++) {
                System.out.print("-");
            }

            if (project.getParent() != null) {
                System.out.println(project.toString() + " --- " +project.getToken() + " child:" + project.getChildCount()  + " data:" + ((ProjectTreeNode) project.getParent()).getToken());
            }
            if (project.getAllowsChildren()) {
                displayAllProjects(project.children, pos + 1);
            }


        }

    }
}        
