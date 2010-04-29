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
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.UIUtil;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.EntityType;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.integration.idea.actions.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.Map.*;
import java.util.List;

public class TasksComponent implements ProjectComponent {

    private static final Logger LOG = Logger.getLogger(TasksComponent.class);
    @NonNls
    public static final String TOOL_WINDOW_ID = "V1Integration";

    private TasksTable table;

    public final Project project;

    private Content content;
    private final WorkspaceSettings cfg;
    private final IDataLayer dataLayer;
    private TableModelListener tableChangesListener;
    private TreeSelectionListener tableSelectionListener;


    public TasksComponent(Project project, WorkspaceSettings settings) {
        this.project = project;
        cfg = settings;
        Configuration config = Configuration.getInstance();
        config.fill();
        dataLayer = ApiDataLayer.getInstance();
        addWorkitemProperties();

        try {
            dataLayer.connect(cfg.v1Path, cfg.user, cfg.passwd, cfg.isWindowsIntegratedAuthentication);
            dataLayer.setCurrentProjectId(cfg.projectToken);
            dataLayer.setShowAllTasks(cfg.isShowAllTask);
            settings.projectToken = dataLayer.getCurrentProjectId();
            settings.projectName = com.versionone.common.sdk.Project.getNameById(dataLayer.getProjectTree(), settings.projectToken);
        } catch (DataLayerException ex) {
            ex.printStackTrace();
        }
        //TODO remove. Actions must get Project from AnActionEvent.DataContext
        if (project != null && !project.isDefault()) {
            ActionManager actionManager = ActionManager.getInstance();

            // set settings to the actions
            ((FilterAction) actionManager.getAction("SelectProject")).setSettings(cfg);
            ((ShowAllItemFilterAction) actionManager.getAction("showAllTaskFilter")).setSettings(cfg);

            // set projects to the actions
            ((SaveDataAction) actionManager.getAction("V1.SaveData")).setProject(project);
            ((RefreshAction) actionManager.getAction("V1.toolRefresh")).setProject(project);
            ((FilterAction) actionManager.getAction("SelectProject")).setProject(project);

            // set Data Layer to actions
            ((ShowAllItemFilterAction) actionManager.getAction("showAllTaskFilter")).setDataLayer(dataLayer);
            ((AddDefectAction) actionManager.getAction("V1.AddDefect")).setDataLayer(dataLayer);
            ((AddDefectAction) actionManager.getAction("V1.ContextMenu.AddDefect")).setDataLayer(dataLayer);
            ((AddTaskAction) actionManager.getAction("V1.AddTask")).setDataLayer(dataLayer);
            ((AddTestAction) actionManager.getAction("V1.AddTest")).setDataLayer(dataLayer);
            ((CloseWorkitemAction) actionManager.getAction("V1.Workitem.Close")).setDataLayer(dataLayer);
        }
    }

    private void addWorkitemProperties() {
        final Map<String, Boolean> properties = new HashMap<String, Boolean>();
        properties.put(Workitem.CHECK_QUICK_CLOSE_PROPERTY, false);
        properties.put(Workitem.CHECK_SIGNUP_PROPERTY, false);

        for (Entry<String, Boolean> entry : properties.entrySet()) {
            for (EntityType type : EntityType.values()) {
                if (type.isWorkitem()) {
                    dataLayer.addProperty(entry.getKey(), type, entry.getValue());
                }
            }
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
        if (table == null) {
            table = createTable();
        } else {
            table.updateUI(true);
        }
    }

    public void refresh() {
        try {
            table.updateData();
        } catch (DataLayerException ex) {
            Icon icon = Messages.getErrorIcon();
            Messages.showMessageDialog(ex.getMessage(), "Error", icon);
        }
    }

    public void selectNode(Workitem itemAtNode) {
        if (table != null) {
            table.selectNode(itemAtNode);
        }
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
        table = createTable();
        panel.add(new JScrollPane(table));
        return panel;
    }

    private TasksTable createTable() {
        TasksTable table;
        TasksModel model;

        try {
            List<PrimaryWorkitem> data = dataLayer.getWorkitemTree();
            model = new TasksModel(data, dataLayer);
            table = new TasksTable(model, dataLayer);
        } catch (DataLayerException ex) {
            Messages.showErrorDialog(ex.getMessage(), "Error");
            model = new TasksModel(new ArrayList<PrimaryWorkitem>(0), dataLayer);
            model.setHideColumns(true);
            table = new TasksTable(model, dataLayer);
        }

        table.setRootVisible(false);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));        
        table.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        table.getTree().addTreeSelectionListener(tableSelectionListener);
        table.getTree().setShowsRootHandles(true);
        table.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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

    @NotNull
    public TasksTable getTable() {
        // TODO possibly get rid of this
        return table;
    }

    public void registerTableChangeListener(TableModelListener listener) {
        tableChangesListener = listener;
        if (table != null) {
            table.getModel().addTableModelListener(tableChangesListener);
        }
    }

    public void registerTableSelectListener(TreeSelectionListener selectionListener) {
        if (table != null) {
            table.getTree().removeTreeSelectionListener(tableSelectionListener);
            table.getTree().addTreeSelectionListener(selectionListener);
        }
        tableSelectionListener = selectionListener;
    }

    public Object getCurrentItem() {
        return table.getWorkitemAtRow(table.getSelectedRow());
    }
}
