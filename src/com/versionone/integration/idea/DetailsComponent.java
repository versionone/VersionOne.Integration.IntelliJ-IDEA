/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.Table;
import com.intellij.util.ui.UIUtil;
import com.versionone.common.sdk.APIDataLayer;
import com.versionone.common.sdk.IDataLayer;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class DetailsComponent implements ProjectComponent {

    private static final Logger LOG = Logger.getLogger(DetailsComponent.class);
    @NonNls
    private static final String COMPONENT_NAME = "V1.Details";
    public static final String TOOL_WINDOW_NAME = "V1Details";

    private final Project project;

    private Content content;
    private WorkspaceSettings cfg;
    private Table table;
    private DetailsModel model;


    public DetailsComponent(Project project, WorkspaceSettings settings) {
        System.out.println("DetailsComponent.DetailsComponent() prj=" + project + " settings=" + settings);
        this.project = project;
        cfg = settings;
    }

    public void projectOpened() {
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
        return COMPONENT_NAME;
    }

    private void initToolWindow() {
        IDataLayer dataLayer = project.getComponent(TasksComponent.class).getDataLayer();
        JPanel contentPanel = createContentPanel(dataLayer);

//        ActionGroup actions = (ActionGroup) ActionManager.getInstance().getAction(COMPONENT_NAME);
//        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(COMPONENT_NAME, actions, false);
//        contentPanel.add(toolbar.getComponent(), BorderLayout.LINE_START);

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_NAME, false, ToolWindowAnchor.RIGHT);
        ContentFactory contentFactory;
        contentFactory = ContentFactory.SERVICE.getInstance();
//        contentFactory = PeerFactory.getInstance().getContentFactory();
        content = contentFactory.createContent(contentPanel, null, false);
        toolWindow.getContentManager().addContent(content);
    }

    public void update() {
        if (content != null) {
            content.setDisplayName(cfg.projectName);
        }
        table.revalidate();
        table.repaint();
    }

    private JPanel createContentPanel(IDataLayer dataLayer) {
        model = new DetailsModel(dataLayer);
        table = new DetailsTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtil.getTreeTextBackground());
        panel.add(new JScrollPane(table));
        return panel;
    }

    private void unregisterToolWindow() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(TOOL_WINDOW_NAME);
    }

    public void setCurrentTask(int task) {
        model.setTask(task);
        table.repaint();
    }

    /**
     * Temporary method for testing purposes. TODO delete
     */
    public static void main(String[] args) {
        DetailsComponent plugin = new DetailsComponent(null, new WorkspaceSettings());
        JPanel panel = plugin.createContentPanel(new APIDataLayer(new WorkspaceSettings()));
        JFrame frame = new JFrame("IDEA V1 Plugin - Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(200, 500));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
