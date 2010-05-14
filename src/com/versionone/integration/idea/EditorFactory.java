package com.versionone.integration.idea;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.Workitem;
import com.versionone.integration.idea.editors.MultiValueEditor;
import com.versionone.integration.idea.editors.RichCellEditor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

/**
 * Create editor controls used in plugin views
 */
public class EditorFactory {
    public static DefaultCellEditor createTextFieldEditor(boolean isEditable) {
        final JTextField textField = new JTextField();
        textField.setEditable(isEditable);
        textField.setEnabled(true);
        textField.setFocusable(true);
        textField.setBorder(new LineBorder(Color.black));

        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Copy");
        menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textField.copy();
            }
        });
        menu.add(menuItem1);
        textField.add(menu);

        MouseListener popupListener = new ContextMenuMouseListener(menu);
        textField.addMouseListener(popupListener);

        return new DefaultCellEditor(textField);
    }

    public static DefaultCellEditor createComboBoxEditor(Workitem item, String attribute, Object currentValue) {
        final PropertyValues values = getAvailableValues(attribute, item);
        final JComboBox comboEditor = new JComboBox(values.toArray());
        comboEditor.setSelectedItem(currentValue);
        comboEditor.setBorder(null);
        return new DefaultCellEditor(comboEditor);
    }

    public static TableCellEditor createMultivalueEditor(Workitem item, IDataLayer dataLayer, String attribute, JTable table) {
        return new MultiValueEditor(dataLayer, item, attribute, table);
    }

    public static RichCellEditor createRichEditor(Workitem item, String attribute, JTable table) {
        return new RichCellEditor(item, attribute, table);
    }

    private static PropertyValues getAvailableValues(String attribute, Workitem item) {
        return ApiDataLayer.getInstance().getListPropertyValues(item.getType(), attribute);
    }
}
