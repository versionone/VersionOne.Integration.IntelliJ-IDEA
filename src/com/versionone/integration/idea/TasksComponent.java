/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.UIUtil;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.EntityType;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.integration.idea.actions.*;
import com.versionone.integration.idea.actions.AbstractAction;
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

public class TasksComponent extends AbstractComponent{

    private static final Logger LOG = Logger.getLogger(TasksComponent.class);

    @NonNls
    private static final String COMPONENT_NAME = "V1.ToolWindow";

    @NonNls
    public static final String TOOL_WINDOW_ID = "V1Workitems";

    private Content content;
    private TasksTable table;
    private TasksModel model;
    private TreeSelectionListener tableSelectionListener;
    private TableModelListener tableChangingListener;

    public TasksComponent(Project project, WorkspaceSettings settings) {
        super(project, settings);
        Configuration config = Configuration.getInstance();
        config.fill();
    }

    @NotNull
    @NonNls
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public void initComponent() {
        ColorKey.createColorKey("V1_CHANGED_ROW", new Color(255, 243, 200));
        addWorkitemProperties();
        createTable();
    }

    @Override
    public void projectOpened() {
        initToolWindow();
        if (getSettings().isEnabled) {
            registerTool();
            registerTableListener();
        }
    }

    @Override
    public void registerTool() {
        try {
            createConnection();
        } catch (DataLayerException ex) {
            LOG.warn(ex.getMessage());
            Icon icon = Messages.getErrorIcon();
            Messages.showMessageDialog("Can not connect to VersionOne", "Error", icon);
        }
        registerToolWindow();
        update();
    }

    @Override
    public void unregisterTool() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(getProject());
        if (toolWindowManager.getToolWindow(TOOL_WINDOW_ID) != null) {
            toolWindowManager.unregisterToolWindow(TOOL_WINDOW_ID);
        }
    }

    @Override
    public void registerTableListener() {
        TableModelListener listener = new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                updateUI();
            }
        };
        getProject().getComponent(DetailsComponent.class).registerTableListener(listener);
    }

    @Override
    public void update() {
        try {
            table.updateData();
            model.setHideColumns(false);
        } catch (DataLayerException ex) {
            Icon icon = Messages.getErrorIcon();
            Messages.showMessageDialog(ex.getMessage(), "Error", icon);
            model.setHideColumns(true);
        }
        updateUI();
    }

    public void updateUI() {
        content.setDisplayName(getSettings().projectName);
        table.updateUI(true);
    }

    public void selectNode(Workitem itemAtNode) {
        table.selectNode(itemAtNode);
    }

    public Object getCurrentItem() {
        return table.getWorkitemAtRow(table.getSelectedRow());
    }

    public void removeEdition() {
        if (table.isEditing()) {
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

    @NotNull
    public TasksTable getTable() {
        // TODO possibly get rid of this
        return table;
    }

    public void registerTableChangeListener(TableModelListener listener) {
        table.getModel().addTableModelListener(tableChangingListener);
        table.getModel().addTableModelListener(listener);
        tableChangingListener = listener;
    }

    public void registerTableSelectListener(TreeSelectionListener selectionListener) {
        table.getTree().removeTreeSelectionListener(tableSelectionListener);
        table.getTree().addTreeSelectionListener(selectionListener);
        //TODO why we need to re-register
        tableSelectionListener = selectionListener;
    }

    JPanel createContentPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtil.getTreeTextBackground());
        panel.add(new JScrollPane(table));
        return panel;
    }

    private void createConnection() throws DataLayerException {
        getDataLayer().connect(getConnectionSettings());
        getDataLayer().setCurrentProjectId(getSettings().projectToken);
        getDataLayer().setShowAllTasks(getSettings().isShowAllTask);
        getSettings().projectToken = getDataLayer().getCurrentProjectId();
        getSettings().projectName = com.versionone.common.sdk.Project.getNameById(getDataLayer().getProjectTree(),
                                                                             getSettings().projectToken);
    }

    private void registerToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(getProject());
        if (toolWindowManager.getToolWindow(TOOL_WINDOW_ID) == null && !getProject().isDefault()) {
            ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, false,
                                                                         ToolWindowAnchor.BOTTOM);
            toolWindow.getContentManager().addContent(content);
        }
    }

    private TasksTable createTable() {
        model = new TasksModel(new ArrayList<PrimaryWorkitem>(0), getDataLayer());
        model.setHideColumns(true);
        table = new TasksTable(model, getDataLayer());
        table.setRootVisible(false);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //TODO maybe delete
        table.getTree().addTreeSelectionListener(tableSelectionListener);
        table.getTree().setShowsRootHandles(true);
        table.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        return table;
    }

    private void initToolWindow() {
        JPanel contentPanel = createContentPanel();
        contentPanel.add(initActions().getComponent(), BorderLayout.LINE_START);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        content = contentFactory.createContent(contentPanel, getSettings().projectName, false);
    }

    private ActionToolbar initActions() {
        ActionManager actionManager = ActionManager.getInstance();

        ((ShowAllItemFilterAction) actionManager.getAction("V1.ShowAllTaskFilter")).setDataLayer(getDataLayer());
        ((ShowAllItemFilterAction) actionManager.getAction("V1.ShowAllTaskFilter")).setSettings(getSettings());

        //TODO get actions by group from plugin.xml
        String[] actionsList = new String[]{"V1.AddDefect", "V1.AddTask", "V1.AddTest",
                                            "V1.SelectProject", "V1.toolRefresh", "V1.SaveData", "V1.Help",
                                            "V1.Workitem.Close", "V1.Workitem.QuickClose", "V1.Workitem.Signup",
                                            "V1.ContextMenu.AddDefect", "V1.ContextMenu.AddTest",
                                            "V1.ContextMenu.AddTask", "V1.ContextMenu.AddDefect"};
        for (String actionName : actionsList) {
            // set Data Layer to actions
            ((AbstractAction) actionManager.getAction(actionName)).setDataLayer(getDataLayer());
            // set settings to actions
            ((AbstractAction) actionManager.getAction(actionName)).setSettings(getSettings());
        }

        ActionGroup actions = (ActionGroup) actionManager.getAction("V1.ToolWindow");
        return actionManager.createActionToolbar("V1.ToolWindow", actions, false);
    }

    private void addWorkitemProperties() {
        final Map<String, Boolean> properties = new HashMap<String, Boolean>();
        properties.put(Workitem.CHECK_QUICK_CLOSE_PROPERTY, false);
        properties.put(Workitem.CHECK_SIGNUP_PROPERTY, false);
        properties.put(Workitem.ORDER_PROPERTY, false);

        for (Entry<String, Boolean> entry : properties.entrySet()) {
            for (EntityType type : EntityType.values()) {
                if (type.isWorkitem()) {
                    getDataLayer().addProperty(entry.getKey(), type, entry.getValue());
                }
            }
        }
    }
}
