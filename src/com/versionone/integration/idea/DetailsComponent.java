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

public class DetailsComponent implements ProjectComponent {

    //private static final Logger LOG = Logger.getLogger(DetailsComponent.class);
    @NonNls
    private static final String COMPONENT_NAME = "V1.Details";
    public static final String TOOL_WINDOW_NAME = "V1Details";

    private final Project project;

    private Table table;
    private DetailsModel model;
    private WorkspaceSettings settings;
    private TableModelListener tableChangesListener;
    private boolean initToolWindow = false;


    public DetailsComponent(Project project, WorkspaceSettings settings) {
        this.settings = settings;
        this.project = project;
    }

    public void projectOpened() {
        registerToolWindow();
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

        public boolean showDetails(boolean isShow) {
        table.setVisible(isShow);
        return isShow;
    }

    public void update() {
        showDetails(settings.isEnable);
        table.revalidate();
        table.repaint();
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
        tableChangesListener = listener;
        if (table != null) {
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

    private void registerToolWindow() {
        if (!initToolWindow) {
            final TasksComponent tasksComponent = project.getComponent(TasksComponent.class);
            if (tasksComponent == null) {
                throw new IllegalStateException("Cannot access " + TasksComponent.TOOL_WINDOW_ID + " component." +
                        " Maybe you are using wrong version of VersionOne plugin.");
            }
            IDataLayer dataLayer = tasksComponent.getDataLayer();
            JPanel contentPanel = createContentPanel(dataLayer);

            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_NAME, false, ToolWindowAnchor.RIGHT);
            ContentFactory contentFactory;
            contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(contentPanel, null, false);
            toolWindow.getContentManager().addContent(content);

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
            initToolWindow = true;
        }
    }

    private void unregisterToolWindow() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(TOOL_WINDOW_NAME);
        initToolWindow = false;
    }

}
