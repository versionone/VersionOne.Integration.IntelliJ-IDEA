/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.util.enumeration.ArrayEnumeration;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.ArrayList;

public class ProjectTreeNode implements TreeNode {
    private final ProjectTreeNode parent;
    private final int index;
    private String name;
    private String token;

    public ArrayList<ProjectTreeNode> children = new ArrayList<ProjectTreeNode>();

    public ProjectTreeNode(String projectName, ProjectTreeNode parent, int index, String token) {
        this.name = projectName;
        this.parent = parent;
        this.index = index;
        this.token = token;
    }

    public TreeNode getChildAt(int childIndex) {
        return this.children.get(childIndex);
    }

    public String getToken() {
        return this.token;
    }

    public int getChildCount() {
        return children.size();
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        return index;
    }

    public boolean getAllowsChildren() {
        return children.size() > 0;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public Enumeration children() {
        return new ArrayEnumeration(children.toArray());
    }

    @Override
    public String toString() {
        return name;
    }
}
