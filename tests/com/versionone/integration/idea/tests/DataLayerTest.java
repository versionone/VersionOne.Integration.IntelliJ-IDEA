/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea.tests;

import com.versionone.integration.idea.DataLayer;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

public class DataLayerTest {
    private DataLayer data;

    @Before
    public void before() {
        data = DataLayer.getInstance();
    }

    @Test
    @Ignore
    public void testGetMainData() {
        Object[][] x = data.getMainData();
        for (Object[] objects : x) {
            for (Object o : objects) {
                System.out.print(o + "|");
            }
            System.out.print("\n");
        }
    }
}        
