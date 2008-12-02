/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.TasksProperties;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

/**
 *
 */
public abstract class AbstractModel extends AbstractTableModel {

    protected final IDataLayer data;
    private final TasksProperties[] properties;
    private final TasksProperties[] propertiesWithEffort;

    public AbstractModel(IDataLayer data, TasksProperties[] properties, TasksProperties[] propertiesWithEffort) {
        this.properties = properties;
        this.propertiesWithEffort = propertiesWithEffort;
        this.data = data;
    }

    public int getPropertiesCount() {
        return data.isTrackEffort() ? propertiesWithEffort.length : properties.length;
    }

    public abstract Vector<String> getAvailableValuesAt(int rowIndex, int columnIndex);

    public abstract String getColumnName(int column);

    public abstract boolean isCellEditable(int rowIndex, int columnIndex);

    protected TasksProperties getProperty(int index) {
        return data.isTrackEffort() ? propertiesWithEffort[index] : properties[index];
    }

    public abstract boolean isRowChanged(int row);

    public TableCellEditor getCellEditor(int row, int col) {
        final Vector<String> values = getAvailableValuesAt(row, col);
        if (values != null) {
            final JComboBox comboEditor = new JComboBox(values);

            //select current value
            comboEditor.setSelectedItem(getValueAt(row, col));
            comboEditor.setBorder(null);
            return new DefaultCellEditor(comboEditor);
        } else {
            // create text field for ID
            final JTextField textFild = new JTextField();
            textFild.setEditable(getProperty(col).isEditable);
            textFild.setEnabled(true);
            textFild.setFocusable(true);
            textFild.setBorder(new LineBorder(Color.black));
            // popup menu with copy functionality
            JPopupMenu menu = new JPopupMenu();
            JMenuItem menuItem1 = new JMenuItem("Copy");
            menuItem1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Get the clipboard
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    // Set the sent text as the new content of the clipboard
                    //clipboard.setContents(new StringSelection(textFild.getText()), null);
                    textFild.copy();
                }
            });
            menu.add(menuItem1);
            textFild.add(menu);

            MouseListener popupListener = new PopupListener(menu);
            textFild.addMouseListener(popupListener);

            return new DefaultCellEditor(textFild);
        }
    }

    /**
     * Listens for debug window popup dialog events.
     */
    private static class PopupListener extends MouseAdapter {
        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}