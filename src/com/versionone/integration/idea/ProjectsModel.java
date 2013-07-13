package com.versionone.integration.idea;

import com.versionone.common.sdk.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.LinkedList;
import java.util.List;

public class ProjectsModel implements TreeModel {

    private final ProjectWrapper root;

    public ProjectsModel(@NotNull List<Project> roots) {
        root = new ProjectWrapper(roots);
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        return ((ProjectWrapper) parent).children[index];
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

    @Nullable
    public TreePath getPathById(String id) {
        List<ProjectWrapper> path = root.getPathById(id);
        if (path == null) {
            return null;
        }
        return new TreePath(path.toArray());
    }

    public static class ProjectWrapper {

        public final ProjectWrapper[] children;
        public final String name;
        public final String id;
        public final int index;

        public ProjectWrapper(@NotNull List<Project> roots) {
            index = -1;
            name = id = "";
            children = new ProjectWrapper[roots.size()];
            int i=0;
            for (Project prj : roots) {
                children[i] = new ProjectWrapper(prj, i);
                i++;
            }
        }

        public ProjectWrapper(@NotNull Project project, int index) {
            name = (String) project.getProperty(Project.NAME_PROPERTY);
            id = project.getId();
            this.index = index;
            children = new ProjectWrapper[project.children.size()];
            int i=0;
            for (Project prj : project.children) {
                children[i] = new ProjectWrapper(prj, i);
                i++;
            }
        }

        @Nullable
        private LinkedList<ProjectWrapper> getPathById(String id) {
            if (this.id.equals(id)) {
                LinkedList<ProjectWrapper> res = new LinkedList<ProjectWrapper>();
                res.add(this);
                return res;
            }
            for (ProjectWrapper child : children) {
                LinkedList<ProjectWrapper> res = child.getPathById(id);
                if (res != null){
                    res.addFirst(this);
                    return res;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}