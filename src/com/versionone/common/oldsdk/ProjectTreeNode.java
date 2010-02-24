/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.oldsdk;

import com.intellij.util.enumeration.ArrayEnumeration;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;

public class ProjectTreeNode implements TreeNode {
    private final ProjectTreeNode parent;
    private final int index;
    private final String name;
    private final String token;

    final ArrayList<ProjectTreeNode> children = new ArrayList<ProjectTreeNode>();

    ProjectTreeNode(String projectName, ProjectTreeNode parent, int index, String token) {
        this.name = projectName;
        this.parent = parent;
        this.index = index;
        this.token = token;
    }

    ProjectTreeNode(String projectName, String token, ProjectTreeNode parent, int index) {
        this(projectName, parent, index, token);
    }

    ProjectTreeNode() {
        this(null, null, 0, null);
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
