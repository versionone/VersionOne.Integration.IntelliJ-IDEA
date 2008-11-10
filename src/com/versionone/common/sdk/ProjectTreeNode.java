package com.versionone.common.sdk;

import java.util.ArrayList;

/**
 * Implementation of IProjectTreeNode
 * @author jerry
 */
public class ProjectTreeNode implements IProjectTreeNode {

	private String name;
	private String token;
	public ArrayList<IProjectTreeNode> children = new ArrayList<IProjectTreeNode>();
	
	public ProjectTreeNode(String name, String token) {
		this.name = name;
		this.token = token;
	}
	
	public IProjectTreeNode[] getChildren() {		
		return children.toArray(new IProjectTreeNode[children.size()]);
	}

	public String getName() {
		return name;
	}

	public String getToken() {
		return token;
	}

	public boolean hasChildren() {
		return 0 != children.size();
	}
	
	public void addChild(IProjectTreeNode value) {
		children.add(value);
	}

	@Override
	public boolean equals(Object obj) {
		boolean rc = false;
		if(null != obj && obj instanceof ProjectTreeNode) {
			ProjectTreeNode node = (ProjectTreeNode)obj;
			rc = token.equals(node.getToken());
		}
		return rc;
	}
}
