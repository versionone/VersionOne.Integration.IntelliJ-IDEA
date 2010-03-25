/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.UIUtil;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.integration.idea.actions.FilterAction;
import com.versionone.integration.idea.actions.Refresh;
import com.versionone.integration.idea.actions.SaveData;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;

public class TasksComponent implements ProjectComponent {

    private static final Logger LOG = Logger.getLogger(TasksComponent.class);
    @NonNls
    public static final String TOOL_WINDOW_ID = "V1Integration";

    public final Project project;

    private Content content;
    private final WorkspaceSettings cfg;
    private TasksTable table;
    private final IDataLayer dataLayer;
    private TableModelListener tableChangesListener;
    private TreeSelectionListener tableSelectionListener;


    public TasksComponent(Project project, WorkspaceSettings settings) {
        this.project = project;
        cfg = settings;
        dataLayer = ApiDataLayer.getInstance();
        Configuration config = Configuration.getInstance();
        config.fill();

        //TODO remove. Actions must get Project from AnActionEvent.DataContext
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
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        JPanel contentPanel = createContentPanel();

        //Adding ActionToolbar
        ActionGroup actions = (ActionGroup) ActionManager.getInstance().getAction("V1.ToolWindow");
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("V1.ToolWindow", actions, false);
        contentPanel.add(toolbar.getComponent(), BorderLayout.LINE_START);

        ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.BOTTOM);
        ContentFactory contentFactory;
        contentFactory = ContentFactory.SERVICE.getInstance();
        content = contentFactory.createContent(contentPanel, cfg.projectName, false);
        toolWindow.getContentManager().addContent(content);
    }

    public void update() {
        if (content != null) {
            content.setDisplayName(cfg.projectName);
        }
        table.getTree().revalidate();
        table.getTree().updateUI();
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

    JPanel createContentPanel() {
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
        try {
            dataLayer.connect(cfg.v1Path, cfg.user, cfg.passwd, cfg.isWindowsIntegratedAuthentication);
        } catch (DataLayerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //final TasksTable table = new TasksTable(new TasksModel(dataLayer.getWorkitemTree()));
        final TasksTable table = new TasksTable(new TasksModel(dataLayer.getWorkitemTree()));
        table.setRootVisible(false);
        table.setShowGrid(false);
        table.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //table.getModel().addTableModelListener(tableChangesListener);
        //table.getSelectionModel().addListSelectionListener(tableSelectionListener);
        table.getTree().addTreeSelectionListener(tableSelectionListener);
        table.getTree().setShowsRootHandles(true);
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

    public void registerTableChangeListener(TableModelListener listener) {
        tableChangesListener = listener;
        if (table != null) {
            table.getModel().addTableModelListener(tableChangesListener);
        }
    }

    /*
    public void registerTableSelectListener(ListSelectionListener selectionListener) {
        tableSelectionListener = selectionListener;
        if (table != null) {
            table.getSelectionModel().addListSelectionListener(tableSelectionListener);
        }
    }*/

    public void registerTableSelectListener(TreeSelectionListener selectionListener) {
        if (table != null) {
            table.getTree().removeTreeSelectionListener(tableSelectionListener);
            table.getTree().addTreeSelectionListener(selectionListener);
        }
        tableSelectionListener = selectionListener;
    }
}
