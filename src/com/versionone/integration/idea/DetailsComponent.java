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
import com.versionone.common.sdk.IDataLayer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.*;

public class DetailsComponent implements ProjectComponent, ToolService {

    @NonNls
    private static final String COMPONENT_NAME = "V1.Details";
    public static final String TOOL_WINDOW_NAME = "V1Details";

    private final Project project;

    private Table table;
    private DetailsModel model;
    private WorkspaceSettings settings;
    private TableModelListener tableChangesListener;


    public DetailsComponent(Project project, WorkspaceSettings settings) {
        this.settings = settings;
        this.project = project;
    }

    public void registerTool() {
        initToolWindow();
    }

    public void unregisterTool() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(TOOL_WINDOW_NAME);
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
        if (table != null) {
            table.revalidate();
            table.repaint();
        }
    }

    public void setItem(Object obj) {
        model.setWorkitem(obj);
    }

    public void removeEdition() {
        if (table != null && table.isEditing()) {
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

    public void registerTableListener(TableModelListener listener) {
        //TODO
        if (table != null) {
            tableChangesListener = listener;
            table.getModel().addTableModelListener(tableChangesListener);
        }
    }

    JPanel createContentPanel(IDataLayer dataLayer) {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtil.getTreeTextBackground());
        model = new DetailsModel(dataLayer);
        table = new DetailsTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table));
        table.getModel().addTableModelListener(tableChangesListener);
        return panel;
    }

    private void initToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        if (toolWindowManager.getToolWindow(TOOL_WINDOW_NAME) == null) {
            ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_NAME, false,
                                                                         ToolWindowAnchor.RIGHT);

            final TasksComponent tasksComponent = project.getComponent(TasksComponent.class);
            if (tasksComponent == null) {
                throw new IllegalStateException("Cannot access " + TasksComponent.TOOL_WINDOW_ID + " component." +
                            " Maybe you are using wrong version of VersionOne plugin.");
            }
            IDataLayer dataLayer = tasksComponent.getDataLayer();
            JPanel contentPanel = createContentPanel(dataLayer);

            ContentFactory contentFactory;
            contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(contentPanel, null, false);
            toolWindow.getContentManager().addContent(content);

            registerTableListener();
        }
    }

    private void registerTableListener() {
        TableModelListener changeListener = new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                update();
            }
        };

        TreeSelectionListener selectListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e){
                removeEdition();
                if (!e.isAddedPath()) {
                    model.setWorkitem(null);
                } else {
                    model.setWorkitem(e.getNewLeadSelectionPath().getLastPathComponent());
                }
                update();
            }
        };

        this.project.getComponent(TasksComponent.class).registerTableChangeListener(changeListener);
        this.project.getComponent(TasksComponent.class).registerTableSelectListener(selectListener);
    }
}
