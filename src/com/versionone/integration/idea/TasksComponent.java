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
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.UIUtil;
import com.versionone.common.oldsdk.APIDataLayer;
import com.versionone.common.oldsdk.IDataLayer;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.integration.idea.actions.FilterAction;
import com.versionone.integration.idea.actions.Refresh;
import com.versionone.integration.idea.actions.SaveData;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;

public class TasksComponent implements ProjectComponent {

    private static final Logger LOG = Logger.getLogger(TasksComponent.class);
    @NonNls
    public static final String TOOL_WINDOW_ID = "V1Integration";

    private final Project project;

    private Content content;
    private final WorkspaceSettings cfg;
    private TasksTable table;
    private final IDataLayer dataLayer;
    private TableModelListener tableChangesListener;
    private ListSelectionListener tableSelectionListener;


    public TasksComponent(Project project, WorkspaceSettings settings) {
        this.project = project;
        cfg = settings;
        dataLayer = new APIDataLayer(cfg);

        if (project != null && !project.isDefault()) {
            ActionManager actions = ActionManager.getInstance();
            ((FilterAction) actions.getAction("Filter")).setSettings(cfg);
            //set projects to the actions
            ((SaveData) actions.getAction("V1.SaveData")).setProject(project);
            ((Refresh) actions.getAction("V1.toolRefresh")).setProject(project);
            ((FilterAction) actions.getAction("Filter")).setProject(project);
        }
    }

    public void projectOpened() {
        String ideaVersion = ApplicationInfo.getInstance().getMajorVersion();
        String minorVersion = ApplicationInfo.getInstance().getMinorVersion();
        //TODO remove trace out
        System.out.println("IDEA version = " + ideaVersion + '.' + minorVersion);
        ideaVersion = ApplicationInfo.getInstance().getVersionName();
        //TODO remove trace out
        System.out.println("IDEA name = " + ideaVersion);
        initToolWindow();

        TableModelListener listener = new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                update();
            }
        };
        this.project.getComponent(DetailsComponent.class).registerTableListener(listener);
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
        Configuration config = new Configuration();
        config.fill();

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        JPanel contentPanel = createContentPanel();

        //Adding ActionToolbar
        ActionGroup actions = (ActionGroup) ActionManager.getInstance().getAction("V1.ToolWindow");
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("V1.ToolWindow", actions, false);
        contentPanel.add(toolbar.getComponent(), BorderLayout.LINE_START);

        ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.BOTTOM);
        ContentFactory contentFactory;
        contentFactory = ContentFactory.SERVICE.getInstance();
//        contentFactory = PeerFactory.getInstance().getContentFactory();
        content = contentFactory.createContent(contentPanel, cfg.projectName, false);
        toolWindow.getContentManager().addContent(content);
    }

    public void update() {
        if (content != null) {
            content.setDisplayName(cfg.projectName);
        }
        table.createDefaultColumnsFromModel();
        table.revalidate();
        table.repaint();
    }

    public void removeEdition() {
        if (table != null && table.isEditing()) {
            //table.removeEditor(); cancel editing (without data saving)
            if (SwingUtilities.isEventDispatchThread()) {
                table.getCellEditor().stopCellEditing();
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        table.getCellEditor().stopCellEditing();
                    }
                });
            }
        }
    }

    private JPanel createContentPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtil.getTreeTextBackground());
        try {
            table = createTable();
        } catch (DataLayerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        panel.add(new JScrollPane(table));
        return panel;
    }

    private TasksTable createTable() throws DataLayerException {
        ApiDataLayer dataLayer = ApiDataLayer.getInstance();
        WorkspaceSettings settings = new WorkspaceSettings();
        try {
            dataLayer.connect(settings.v1Path, settings.user, settings.passwd, settings.isWindowsIntegratedAuthentication);
        } catch (DataLayerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //final TasksTable table = new TasksTable(new TasksModel(dataLayer.getWorkitemTree()));
        final TasksTable table = new TasksTable(new TasksModel(dataLayer.getWorkitemTree()));
        table.setRootVisible(false);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //table.getModel().addTableModelListener(tableChangesListener);
        //table.getSelectionModel().addListSelectionListener(tableSelectionListener);
        return table;
    }

    private void unregisterToolWindow() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(TOOL_WINDOW_ID);
    }

    @NotNull
    public IDataLayer getDataLayer() {
        if (dataLayer == null) {
            throw new IllegalStateException("method call before creating object");
        }
        return dataLayer;
    }

    /**
     * Temporary method for testing purposes. TODO delete
     */
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.fill();
        TasksComponent plugin = new TasksComponent(null, new WorkspaceSettings());
        JPanel panel = plugin.createContentPanel();
        JFrame frame = new JFrame("IDEA V1 Plugin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 100));
        frame.add(panel);
        frame.pack();
        frame.show();
        frame.setVisible(true);
    }

    public void registerTableChangeListener(TableModelListener listener) {
        tableChangesListener = listener;
        if (table != null) {
            table.getModel().addTableModelListener(tableChangesListener);
        }
    }

    public void registerTableSelectListener(ListSelectionListener selectionListener) {
        tableSelectionListener = selectionListener;
        if (table != null) {
            table.getSelectionModel().addListSelectionListener(tableSelectionListener);
        }
    }
}
