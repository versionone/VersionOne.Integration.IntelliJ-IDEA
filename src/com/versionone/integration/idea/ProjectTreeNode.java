/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.IProjectTreeNode;
import com.intellij.util.enumeration.ArrayEnumeration;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class ProjectTreeNode implements TreeNode {
    private final IProjectTreeNode prj;
    private final ProjectTreeNode parent;
    private final int index;

    public ProjectTreeNode(IProjectTreeNode myProject, ProjectTreeNode parent, int index) {
        prj = myProject;
        this.parent = parent;
        this.index = index;
    }

    public TreeNode getChildAt(int childIndex) {
        return new ProjectTreeNode(prj.getChildren()[childIndex], this, childIndex);
    }

    public int getChildCount() {
        return prj.getChildren().length;
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        return index;
    }

    public boolean getAllowsChildren() {
        return prj.hasChildren();
    }

    public boolean isLeaf() {
        return !prj.hasChildren();
    }

    public Enumeration children() {
        return new ArrayEnumeration(prj.getChildren());
    }

    @Override
    public String toString() {
        return prj.getName();
    }
}
