package com.versionone.common.sdk;

import com.versionone.apiclient.IAssetType;

import org.junit.Assert;
import org.junit.Test;

import static com.versionone.common.sdk.EntityType.*;

/**
 * This is unit test and it must be run as JUnit test (NOT as JUnit Plug-in
 * Test).
 * 
 * @author rozhnev
 */
public class WorkitemTester {

    @Test
    public void StoryConstructorTest() {
        ApiDataLayer data = ApiDataLayer.getInstance();
        data.setShowAllTasks(true);
        IAssetType storyType = new AssetTypeMock(Story.name());
        IAssetType taskType = new AssetTypeMock(Story.name());
        AssetMock story = new AssetMock(storyType);
        AssetMock task = new AssetMock(taskType);
        story.children.add(task);
        PrimaryWorkitem item1 = new PrimaryWorkitem(data, story);
        SecondaryWorkitem item11 = new SecondaryWorkitem(data, task, item1);
        Assert.assertEquals(item1, item11.parent);
        Assert.assertTrue(item1.children.contains(item11));
    }

    @Test
    public void ProjectConstructorTest() {
        ApiDataLayer data = ApiDataLayer.getInstance();
        data.setShowAllTasks(false);
        IAssetType prjType = new AssetTypeMock(Scope.name());
        AssetMock asset1 = new AssetMock(prjType);
        AssetMock asset11 = new AssetMock(prjType);
        asset1.children.add(asset11);
        Project item1 = new Project(data, asset1);
        Project item11 = new Project(data, asset11, item1);
        Assert.assertEquals(null, item1.parent);
        Assert.assertEquals(item1, item11.parent);
        Assert.assertTrue(item1.children.contains(item11));
        Assert.assertEquals(0, item11.children.size());
    }
}