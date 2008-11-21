/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.tests;

import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.IDataLayer;
import com.versionone.integration.idea.ProjectTreeNode;
import com.versionone.integration.idea.TasksProperties;
import com.versionone.integration.idea.WorkspaceSettings;

import org.junit.Before;
import org.junit.Test;

import java.net.ConnectException;
import java.util.ArrayList;

public class DataLayerTest {
    private IDataLayer data;

    @Before
    public void before() {
        final WorkspaceSettings settings = new WorkspaceSettings();
        settings.v1Path = "http://eval.versionone.net/ExigenTest/";
        settings.user = "admin";
        settings.passwd = "admin";
        settings.projectName = "System (All Projects)";
        data = new DataLayer(settings);
    }

    /**
     * Integrational test. Need access to V1 server.
     */
    @Test
    public void testGetMainData() {
        int list = data.getTasksCount();
        for (int i = 0; i < list; i++) {
            for (TasksProperties property : TasksProperties.values()) {
                System.out.println(data.getTaskPropertyValue(i, property));
            }
        }
    }

    /**
     * Integration test. Need access to V1 server.
     */
    @Test
    public void testGetProjects() throws ConnectException {
        com.versionone.integration.idea.ProjectTreeNode projects = data.getProjects();

        //for(Project project : projects) {

        System.out.println(projects.toString() + " --- " + projects.getToken() + " child:" + projects.getChildCount());
        displayAllProjects(projects.children, 0);

        //}
    }

    private void displayAllProjects(ArrayList<ProjectTreeNode> projectTreeNodes, int pos) {
        for (ProjectTreeNode project : projectTreeNodes) {
            System.out.print(pos);
            for (int i = 0; i < pos * 2; i++) {
                System.out.print("-");
            }

            if (project.getParent() != null) {
                System.out.println(project.toString() + " --- " + project.getToken() + " child:" + project.getChildCount() + " data:" + ((ProjectTreeNode) project.getParent()).getToken());
            }
            if (project.getAllowsChildren()) {
                displayAllProjects(project.children, pos + 1);
            }
        }
    }

//    @Test
//    public void sdkTest() {
//        WorkspaceSettings cfg = WorkspaceSettings.getInstance();
//        V1Instance v1 = new V1Instance(cfg.v1Path, cfg.user, cfg.passwd);
//        Project project = v1.get().projectByName(cfg.projectName);
//        final Collection<Project> childProjects = project.getThisAndAllChildProjects();
//        final TaskFilter filter = new TaskFilter();
//
//        for (Project prj : childProjects) {
//            if (prj.isActive()) {
//                filter.project.add(prj);
//                break;
//            }
//        }
//        filter.getState().add(BaseAssetFilter.State.Active);
//        Collection<Task> tasks = v1.get().tasks(filter);
//
//        for (Task task : tasks) {
//
//            for (int i=0; i<100;i++){
//                String name = task.getName();
//
//                System.out.println(name);
//            }
//        }
//    }

    /**
     * Temporary method for testing purposes. TODO delete
     */
/*
    public static void main(String[] args) throws ConnectException {
        TasksComponent plugin = new TasksComponent(null);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JTree(DataLayer.getInstance().getProjects()));
        JFrame frame = new JFrame("IDEA V1 Plugin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(200, 800));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
*/
}
