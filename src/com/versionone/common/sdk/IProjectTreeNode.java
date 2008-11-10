package com.versionone.common.sdk;

/**
 * Interface for ProjectTreeNode
 * @author Jerry D. Odenwelder Jr.
 *
 */
public interface IProjectTreeNode {

	/**
	 * Get the name of this node
	 * @return Name of Node
	 */
	String getName();
	
	/**
	 * Get the token for this Node
	 * @return token for node
	 */
	String getToken();
	
	/**
	 * Does this node have any children
	 */
	boolean hasChildren();
	
	/**
	 * Return the children for this node
	 * @return IProjectTreeNode for children 
	 */
	IProjectTreeNode[] getChildren();
}
