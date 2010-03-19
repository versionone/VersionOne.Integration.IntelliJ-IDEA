/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.PropertyValues;

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

/**
 *
 */
public abstract class AbstractModel extends AbstractTableModel {

    protected final IDataLayer data;
    protected final Configuration configuration;

    public AbstractModel(IDataLayer data) {
        this.data = data;
        configuration = Configuration.getInstance();
    }

    public abstract PropertyValues getAvailableValuesAt(int rowIndex, int columnIndex);

    public abstract String getColumnName(int column);

    public abstract boolean isCellEditable(int rowIndex, int columnIndex);

    protected abstract Configuration.ColumnSetting getProperty(int rowIndex, int columnIndex);

    public abstract boolean isRowChanged(int row);

    public TableCellEditor getCellEditor(int row, int col) {
        Workitem item = getWorkitem();
        //item.getProperty(.attribute);
        if (getProperty(row, col).type.equals("String")  || getProperty(row, col).type.equals("Effort")) {
            return createTextField(getProperty(row, col).readOnly || item.isPropertyReadOnly((getProperty(row, col).attribute)));
        } else if (getProperty(row, col).type.equals("List")) {
            final PropertyValues values = getAvailableValuesAt(row, col);
            final JComboBox comboEditor = new JComboBox(values.toArray());

            //select current value
            comboEditor.setSelectedItem(getValueAt(row, col));
            comboEditor.setBorder(null);
            return new DefaultCellEditor(comboEditor);
        }

        return createTextField(true);
    }

    private DefaultCellEditor createTextField(boolean isReadOnly) {
        // create text field for ID
        final JTextField textFild = new JTextField();
        textFild.setEditable(!isReadOnly);
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

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (!getProperty(rowIndex, columnIndex).readOnly) {
            getWorkitem().setProperty(getProperty(rowIndex, columnIndex).attribute, aValue);

            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    protected abstract Workitem getWorkitem();


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