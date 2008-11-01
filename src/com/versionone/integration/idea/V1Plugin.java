/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.peer.PeerFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.UIUtil;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.awt.*;

public class V1Plugin implements ProjectComponent {


    private final ColumnData[] columnData = {   new ColumnData("Title", "string", true),
                                                new ColumnData("ID", "number", false),
                                                new ColumnData("Parent", "string", false),
                                                new ColumnData("Detail Estimate", "number", true),
                                                new ColumnData("Done", "number", false),
                                                new ColumnData("Effort", "number", true),
                                                new ColumnData("To Do", "number", true),
                                                new ColumnData("Status", "list", true)};

    private static final int IDEA_VERSION = 7941;
    private static final boolean IDEA8 = IDEA_VERSION > 7941;

    private static final Logger LOG = Logger.getLogger(V1Plugin.class);
    @NonNls public static final String TOOL_WINDOW_ID = "V1Integration";


    private final Project project;

    private ToolWindow toolWindow;
    private JPanel contentPanel;
    private final WorkspaceSettings cfg = new WorkspaceSettings();
    private DataLayer layout = new DataLayer();


    public V1Plugin(Project project) {
        this.project = project;
    }

    public void projectOpened() {
        String ideaVersion = ApplicationInfo.getInstance().getMajorVersion();
        System.out.println("IDEA version = " + ideaVersion);
        initToolWindow();
    }

    public void projectClosed() {
        unregisterToolWindow();
    }

    public void initComponent() {
        // empty
    }

    public void disposeComponent() {
        // empty
    }

    public String getComponentName() {
        return "V1.ToolWindow";
    }

    private void initToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        contentPanel = createContentPanel();
        toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.LEFT);
        ContentFactory contentFactory;
//        contentFactory = ContentFactory.SERVICE.getInstance();
        contentFactory = PeerFactory.getInstance().getContentFactory();
        Content content = contentFactory.createContent(contentPanel, "<Project>", false);
        toolWindow.getContentManager().addContent(content);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(UIUtil.getTreeTextBackground());
        panel.add(new JLabel("Hello World!", JLabel.CENTER), BorderLayout.CENTER);
        JTable table = creatingTable();
        //panel.add(creatingTable());

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        ActionGroup actions = (ActionGroup) ActionManager.getInstance().getAction("V1.ToolWindow");
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("V1.ToolWindow", actions, false);

        panel.add(toolbar.getComponent(), BorderLayout.LINE_START);
        return panel;
    }


    private JTable creatingTable() {

        JTable table = new V1Table(new V1TableModel(layout.getMainData(), columnData));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        //JComboBox cb =new JComboBox(new DataLayer().getAllStatuses());

        //JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        //table.getColumn(table.getColumnName(7)).setCellEditor(new DefaultCellEditor(cb));

        //container.setLayout();
        //container.add(table.getTableHeader(), BorderLayout.PAGE_START);
        //container.add(table, BorderLayout.CENTER);



        return table;

    }



    private void unregisterToolWindow() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(TOOL_WINDOW_ID);
    }

    /**
     * Temporary method for testing purposes.
     */
    public static void main(String[] args) {
        V1Plugin plugin = new V1Plugin(null);
        JPanel panel = plugin.createContentPanel();
        JFrame frame = new JFrame("IDEA V1 Plugin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 100));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }


}
