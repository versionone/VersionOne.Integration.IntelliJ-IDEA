/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.tests;

import com.versionone.integration.idea.DataLayer;
import com.versionone.integration.idea.TasksProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.util.Collection;

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
}        
