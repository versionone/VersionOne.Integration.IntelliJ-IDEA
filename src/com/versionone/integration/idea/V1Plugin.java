/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.State;
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
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class V1Plugin implements ProjectComponent {


    private final String[] columnNames = {  "Title",
                                            "ID",
                                            "Parent",
                                            "Detail Estimate",
                                            "Done",
                                            "Effort",
                                            "To Do",
                                            "Status"};

    private static final int IDEA_VERSION = 7941;
    private static final boolean IDEA8 = IDEA_VERSION > 7941;

    private static final Logger LOG = Logger.getLogger(V1Plugin.class);
    @NonNls public static final String TOOL_WINDOW_ID = "V1Integration";


    private final Project project;

    private ToolWindow toolWindow;
    private JPanel contentPanel;
    private final Settings cfg = new Settings();
    private V1DBLayout layout = new V1DBLayout();


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

        JTable table = new JTable(new V1TableModel(layout.getMainData(), columnNames));


        //JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

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

    @State(name = "V1PluginSettings", storages = {
            @Storage(id = "other",
                    file = "$WORKSPACE_FILE$"
            )})
    public static class Settings implements PersistentStateComponent<Settings> {
        public String user, passwd;

        public Settings getState() {
            return this;
        }

        public void loadState(Settings state) {
            user = state.user;
            passwd = state.passwd;
        }
    }


    public class V1TableModel extends DefaultTableModel {
        public V1TableModel(Object[][] data, String[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
