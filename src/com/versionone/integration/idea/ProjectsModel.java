package com.versionone.integration.idea;

import com.versionone.common.sdk.Project;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;

public class ProjectsModel implements TreeModel {

    private final  List<Project> roots;

    public ProjectsModel(List<Project> roots) {
        this.roots = roots;
    }

    public Object getRoot() {
        return new ProjectWrapper(roots);
    }

    public Object getChild(Object parent, int index) {
        return ((ProjectWrapper) parent).children;
    }

    public int getChildCount(Object parent) {
        return ((ProjectWrapper) parent).children.length;
    }

    public boolean isLeaf(Object node) {
        return ((ProjectWrapper) node).children.length == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getIndexOfChild(Object parent, Object child) {
        int x = ((ProjectWrapper) child).index;
        ProjectWrapper[] children = ((ProjectWrapper) parent).children;
        if (x > 0 && x < children.length && children[x].equals(child)) {
            return x;
        }
        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeTreeModelListener(TreeModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private class ProjectWrapper {

        private final ProjectWrapper[] children;
        private final String name;
        private final int index;

        public ProjectWrapper(List<Project> roots) {
            index = -1;
            name = "";
            children = roots.toArray(new ProjectWrapper[roots.size()]);
            int i=0;
            for (Project prj : roots) {
                children[i] = new ProjectWrapper(prj, i);
                i++;
            }
        }

        public ProjectWrapper(Project project, int index) {
            name = (String) project.getProperty(Project.NAME_PROPERTY);
            this.index = index;
            children = project.children.toArray(new ProjectWrapper[project.children.size()]);
        }
    }
}