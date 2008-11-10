package com.versionone.common.sdk;

import java.util.ArrayList;

/**
 * Implementation of IProjectTreeNode
 * @author jerry
 */
public class ProjectTreeNode implements IProjectTreeNode {

	private String _name;
	private String _token;
	ArrayList<IProjectTreeNode> _children = new ArrayList<IProjectTreeNode>();
	
	public ProjectTreeNode(String name, String token) {
		_name = name;
		_token = token;
	}
	
	public IProjectTreeNode[] getChildren() {		
		return _children.toArray(new IProjectTreeNode[_children.size()]);
	}

	public String getName() {
		return _name;
	}

	public String getToken() {
		return _token;
	}

	public boolean hasChildren() {
		return 0 != _children.size();
	}
	
	public void addChild(IProjectTreeNode value) {
		_children.add(value);
	}

	@Override
	public boolean equals(Object obj) {
		boolean rc = false;
		if(null != obj && obj instanceof ProjectTreeNode) {
			ProjectTreeNode node = (ProjectTreeNode)obj;
			rc = _token.equals(node.getToken());
		}
		return rc;
	}
}
