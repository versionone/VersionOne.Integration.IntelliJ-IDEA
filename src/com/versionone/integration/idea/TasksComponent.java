/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.peer.PeerFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.UIUtil;
import static com.versionone.integration.idea.TasksProperties.DetailEstimeate;
import static com.versionone.integration.idea.TasksProperties.Done;
import static com.versionone.integration.idea.TasksProperties.Effort;
import static com.versionone.integration.idea.TasksProperties.ID;
import static com.versionone.integration.idea.TasksProperties.Parent;
import static com.versionone.integration.idea.TasksProperties.Status;
import static com.versionone.integration.idea.TasksProperties.Title;
import static com.versionone.integration.idea.TasksProperties.ToDo;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class TasksComponent implements ProjectComponent {

    private static final Logger LOG = Logger.getLogger(TasksComponent.class);
    @NonNls
    public static final String TOOL_WINDOW_ID = "V1Integration";
    private static final TasksProperties[] tasksColumnData = {Title, ID, Parent, DetailEstimeate, Done, Effort, ToDo, Status};

    private final Project project;

    private Content content;
    private final WorkspaceSettings cfg = WorkspaceSettings.getInstance();
    private TasksTable table;


    public TasksComponent(Project project) {
        this.project = project;
    }

    public void projectOpened() {
        String ideaVersion = ApplicationInfo.getInstance().getMajorVersion();
        String minorVersion = ApplicationInfo.getInstance().getMinorVersion();
        System.out.println("IDEA version = " + ideaVersion + '.' + minorVersion);
        ideaVersion = ApplicationInfo.getInstance().getVersionName();
        System.out.println("IDEA name = " + ideaVersion);
        initToolWindow();
    }

    public void projectClosed() {
        unregisterToolWindow();
    }

    public void initComponent() {
        ColorKey.createColorKey("V1_CHANGED_ROW", new Color(255, 243, 200));
    }

    public void disposeComponent() {
        // empty
    }

    @NotNull
    @NonNls
    public String getComponentName() {
        return "V1.ToolWindow";
    }

    private void initToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        JPanel contentPanel = createContentPanel();

        ActionGroup actions = (ActionGroup) ActionManager.getInstance().getAction("V1.ToolWindow");
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("V1.ToolWindow", actions, false);
        contentPanel.add(toolbar.getComponent(), BorderLayout.LINE_START);

        ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.BOTTOM);
        ContentFactory contentFactory;
//        contentFactory = ContentFactory.SERVICE.getInstance();
        contentFactory = PeerFactory.getInstance().getContentFactory();
        content = contentFactory.createContent(contentPanel, cfg.projectName, false);
        toolWindow.getContentManager().addContent(content);
    }

    public void updateDisplayName() {
        if (content != null) {
            content.setDisplayName(cfg.projectName);
        }
        revalidate();
    }

    public void revalidate() {
        table.revalidate();
        table.repaint();
    }

    private JPanel createContentPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtil.getTreeTextBackground());
        table = createTable();
        panel.add(new JScrollPane(table));
        return panel;
    }

    private TasksTable createTable() {
        TasksTable table = new TasksTable(new HorizontalTableModel(tasksColumnData));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        return table;
    }

    private void unregisterToolWindow() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(TOOL_WINDOW_ID);
    }

    /**
     * Temporary method for testing purposes. TODO delete
     */
    public static void main(String[] args) {
        TasksComponent plugin = new TasksComponent(null);
        JPanel panel = plugin.createContentPanel();
        JFrame frame = new JFrame("IDEA V1 Plugin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 100));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
