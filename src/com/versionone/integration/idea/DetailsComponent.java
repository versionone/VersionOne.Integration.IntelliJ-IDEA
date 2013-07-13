/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.Table;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.*;

public class DetailsComponent extends AbstractComponent {

    @NonNls
    private static final String COMPONENT_NAME = "V1.Details";
    @NonNls
    public static final String TOOL_WINDOW_ID = "V1Details";

    private Content content;
    private Table table;
    private DetailsModel model;
    private TableModelListener tableChangesListener;

    public DetailsComponent(Project project, WorkspaceSettings settings) {
        super(project, settings);
    }

    @NotNull
    @NonNls
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public void initComponent() {
        ColorKey.createColorKey("V1_CHANGED_ROW", new Color(255, 243, 200));
        createTable();
    }

    @Override
    public void projectOpened() {
        //registerTableListener();
        initToolWindow();
        if (getSettings().isEnabled) {
            registerTool();
            registerTableListener();
        }
    }

    @Override
    public void registerTool() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(getProject());
        if (toolWindowManager.getToolWindow(TOOL_WINDOW_ID) == null && !getProject().isDefault()) {
            ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, false,
                                                                         ToolWindowAnchor.BOTTOM);
            toolWindow.getContentManager().addContent(content);
        }
    }

    @Override
    public void unregisterTool() {
        ToolWindowManager.getInstance(getProject()).unregisterToolWindow(TOOL_WINDOW_ID);
    }

    @Override
    public void registerTableListener() {
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

        getProject().getComponent(TasksComponent.class).registerTableChangeListener(changeListener);
        getProject().getComponent(TasksComponent.class).registerTableSelectListener(selectListener);
    }

    @Override
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
        //TODO why we need to re-register
        table.getModel().removeTableModelListener(tableChangesListener);
        table.getModel().addTableModelListener(listener);
        tableChangesListener = listener;
    }

    JPanel createContentPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtil.getTreeTextBackground());
        panel.add(new JScrollPane(table));
        return panel;
    }

    private void createTable() {
        model = new DetailsModel(getDataLayer());
        table = new DetailsTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getModel().addTableModelListener(tableChangesListener);
    }

    private void initToolWindow() {
//        final AbstractComponent tasksComponent = getProject().getComponent(TasksComponent.class);
//        if (tasksComponent == null) {
//            throw new IllegalStateException("Cannot access " + TasksComponent.TOOL_WINDOW_ID + " component." +
//                    " Maybe you are using wrong version of VersionOne plugin.");
//        }
        JPanel contentPanel = createContentPanel();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        content = contentFactory.createContent(contentPanel, null, false);
    }
}
