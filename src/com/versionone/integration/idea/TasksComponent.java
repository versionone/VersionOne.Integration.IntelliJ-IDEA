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
import com.versionone.integration.idea.actions.AbstractAction;
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

public class TasksComponent implements ProjectComponent, ToolService {

    //private static final Logger LOG = Logger.getLogger(TasksComponent.class);
    @NonNls
    public static final String TOOL_WINDOW_ID = "V1Integration";
    @NonNls
    private static final String COMPONENT_NAME = "V1.ToolWindow";

    public final Project project;

    private Content content;
    private TasksTable table;
    private final WorkspaceSettings settings;
    private final IDataLayer dataLayer;
    private TreeSelectionListener tableSelectionListener;


    public TasksComponent(Project project, WorkspaceSettings settings) {
        this.project = project;
        this.settings = settings;
        Configuration config = Configuration.getInstance();
        config.fill();
        dataLayer = ApiDataLayer.getInstance();
        addWorkitemProperties();
        initActions(settings, dataLayer);
    }

    public void registerTool() {
        try {
            createConnection();
        } catch (DataLayerException ex) {
            ex.printStackTrace();
            Icon icon = Messages.getErrorIcon();
            Messages.showMessageDialog("Can not connect to VersionOne", "Error", icon);
        }
        initToolWindow();
    }

    public void unregisterTool() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(TOOL_WINDOW_ID);
    }

    public void projectOpened() {
        if (settings.isEnabled) {
            registerTool();
        }
    }
    
    public void projectClosed() {
        unregisterTool();
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

    public void update() {
        if (content != null) {
            content.setDisplayName(settings.projectName);
        }
        if (table == null) {
            table = createTable();
        } else {
            table.updateUI(true);
        }
    }

    public void refresh() {
        //if (table != null) {
            try {
                table.updateData();
            } catch (DataLayerException ex) {
                Icon icon = Messages.getErrorIcon();
                Messages.showMessageDialog(ex.getMessage(), "Error", icon);
            }
       // }
    }

    public void selectNode(Workitem itemAtNode) {
        //TODO verify NPE
        table.selectNode(itemAtNode);
    }

    public void removeEdition() {
        //TODO verify NPE
        if (table.isEditing()) {
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
        //TODO verify NPE
        table.getModel().addTableModelListener(listener);
    }

    public void registerTableSelectListener(TreeSelectionListener selectionListener) {
        //TODO verify NPE
        table.getTree().removeTreeSelectionListener(tableSelectionListener);
        table.getTree().addTreeSelectionListener(selectionListener);
        tableSelectionListener = selectionListener;
    }

    public Object getCurrentItem() {
        //TODO verify NPE
        return table.getWorkitemAtRow(table.getSelectedRow());
    }

    JPanel createContentPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtil.getTreeTextBackground());
        table = createTable();
        panel.add(new JScrollPane(table));
        return panel;
    }

    /**
     * Creates connection if not connected
     */
    private void createConnection() throws DataLayerException {
        if (!dataLayer.isConnected()) {
            dataLayer.connect(settings.v1Path, settings.user, settings.passwd,
                              settings.isWindowsIntegratedAuthentication);
            dataLayer.setCurrentProjectId(settings.projectToken);
            dataLayer.setShowAllTasks(settings.isShowAllTask);
            settings.projectToken = dataLayer.getCurrentProjectId();
            settings.projectName = com.versionone.common.sdk.Project.getNameById(dataLayer.getProjectTree(),
                                                                                 settings.projectToken);
        }
    }

    /**
     * Creates tool window if it not exist
     */
    private void initToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        if (toolWindowManager.getToolWindow(TOOL_WINDOW_ID) == null) {
            ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, false,
                                                                         ToolWindowAnchor.BOTTOM);
            JPanel contentPanel = createContentPanel();

            ActionGroup actions = (ActionGroup) ActionManager.getInstance().getAction("V1.ToolWindow");
            ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("V1.ToolWindow", actions, false);
            contentPanel.add(toolbar.getComponent(), BorderLayout.LINE_START);

            ContentFactory contentFactory;
            contentFactory = ContentFactory.SERVICE.getInstance();
            content = contentFactory.createContent(contentPanel, settings.projectName, false);
            toolWindow.getContentManager().addContent(content);

            registerTableListener();
        }
    }

    private void initActions(WorkspaceSettings settings, IDataLayer dataLayer) {
        ActionManager actionManager = ActionManager.getInstance();

        ((ShowAllItemFilterAction) actionManager.getAction("V1.ShowAllTaskFilter")).setDataLayer(dataLayer);
        ((ShowAllItemFilterAction) actionManager.getAction("V1.ShowAllTaskFilter")).setSettings(settings);

        //TODO get actions by group from plugin.xml
        String[] actionsList = new String[]{"V1.AddDefect", "V1.AddTask", "V1.AddTest",
                                            "V1.SelectProject", "V1.toolRefresh", "V1.SaveData", "V1.Help",
                                            "V1.Workitem.Close", "V1.Workitem.QuickClose", "V1.Workitem.Signup",
                                            "V1.ContextMenu.AddDefect", "V1.ContextMenu.AddTest",
                                            "V1.ContextMenu.AddTask", "V1.ContextMenu.AddDefect"};
        for (String actionName : actionsList) {
            // set Data Layer to actions
            ((AbstractAction) actionManager.getAction(actionName)).setDataLayer(dataLayer);
            // set settings to actions
            ((AbstractAction) actionManager.getAction(actionName)).setSettings(settings);
        }
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
        //TODO maybe delete
        table.getTree().addTreeSelectionListener(tableSelectionListener);
        table.getTree().setShowsRootHandles(true);
        table.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        return table;
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

    private void registerTableListener() {
        TableModelListener listener = new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    update();
                }
            };
        this.project.getComponent(DetailsComponent.class).registerTableListener(listener);
    }
}
