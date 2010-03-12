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
import com.intellij.util.ui.treetable.TreeTableModel;
import com.versionone.common.oldsdk.APIDataLayer;
import com.versionone.common.oldsdk.IDataLayer;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;

public class DetailsComponent implements ProjectComponent {

    private static final Logger LOG = Logger.getLogger(DetailsComponent.class);
    @NonNls
    private static final String COMPONENT_NAME = "V1.Details";
    public static final String TOOL_WINDOW_NAME = "V1Details";

    private final Project project;

    private Table table;
    private DetailsModel model;
    private TableModelListener tableChangesListener;


    public DetailsComponent(Project project, WorkspaceSettings settings) {
        this.project = project;
    }

    public void projectOpened() {
        initToolWindow();

        TableModelListener changeListener = new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                update();
            }
        };

        ListSelectionListener selectListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                int index = lsm.getMinSelectionIndex();

                model.setTask(index);
                update();
            }
        };

        this.project.getComponent(TasksComponent.class).registerTableChangeListener(changeListener);
        this.project.getComponent(TasksComponent.class).registerTableSelectListener(selectListener);
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

    public void registerTableListener(TableModelListener listener) {
        tableChangesListener = listener;
        if (table != null) {
            table.getModel().addTableModelListener(tableChangesListener);
        }
    }

    private void initToolWindow() {
        IDataLayer dataLayer = project.getComponent(TasksComponent.class).getDataLayer();
        JPanel contentPanel = createContentPanel(dataLayer);

//        ActionGroup actions = (ActionGroup) ActionManager.getInstance().getAction(COMPONENT_NAME);
//        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(COMPONENT_NAME, actions, false);
//        contentPanel.add(toolbar.getComponent(), BorderLayout.LINE_START);

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_NAME, false, ToolWindowAnchor.RIGHT);
        ContentFactory contentFactory;
        contentFactory = ContentFactory.SERVICE.getInstance();
//        contentFactory = PeerFactory.getInstance().getContentFactory();
        Content content = contentFactory.createContent(contentPanel, null, false);
        toolWindow.getContentManager().addContent(content);
    }

    public void update() {
//        if (content != null) {
//            content.setDisplayName(cfg.projectName);
//        }
        table.revalidate();
        table.repaint();
    }

    private JPanel createContentPanel(IDataLayer dataLayer) {
        model = new DetailsModel(dataLayer);
        table = new TasksTable((TreeTableModel) model);//CastException
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtil.getTreeTextBackground());
        panel.add(new JScrollPane(table));
        table.getModel().addTableModelListener(tableChangesListener);
        return panel;
    }

    private void unregisterToolWindow() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(TOOL_WINDOW_NAME);
    }

    public void setCurrentTask(int task) {
        model.setTask(task);
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

    /**
     * Temporary method for testing purposes. TODO delete
     */
    public static void main(String[] args) {
        DetailsComponent plugin = new DetailsComponent(null, new WorkspaceSettings());
        JPanel panel = plugin.createContentPanel(new APIDataLayer(new WorkspaceSettings()));
        JFrame frame = new JFrame("IDEA V1 Plugin - Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(200, 500));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
