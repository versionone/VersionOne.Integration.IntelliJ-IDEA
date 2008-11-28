package com.versionone.integration.idea;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.util.ui.Table;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.TasksProperties;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 */
public class TasksTable extends Table {

    private final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
    private final IDataLayer data;

    public TasksTable(TasksModel v1TableModel, IDataLayer data) {
        super(v1TableModel);
        this.data = data;
    }

    @Override
    public TasksModel getModel() {
        return (TasksModel) super.getModel();
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int col) {
        if (getModel().getColumnType(col) == TasksProperties.Type.STATUS_LIST) {
            JComboBox comboEditor = new JComboBox(data.getAllStatuses());
            //select current value
            //comboEditor.setSelectedItem(DataLayer.getInstance().getValue(col, row));
            comboEditor.setBorder(null);
            ItemListener listener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    //DataLayer.getInstance().
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        System.out.println("row=" + row + " col=" + col + "----" + e.getItem() + " - " + e.paramString());
                    }
                }
            };

            comboEditor.addItemListener(listener);
            return new DefaultCellEditor(comboEditor);
        } else if (getModel().getColumnName(col).equals("ID")) {
            // create text field for ID
            final JTextField textFild = new JTextField();
            textFild.setEditable(false);
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
        } else {
            return super.getCellEditor(row, col);
        }
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        if (rowIndex != getSelectedRow()) {
            if (getModel().isRowChanged(rowIndex)) {
                c.setBackground(colorsScheme.getColor(ColorKey.find("V1_CHANGED_ROW")));
                c.setForeground(Color.black);
            } else {
                c.setBackground(getBackground());
                c.setForeground(getForeground());
            }
        } else {
            c.setBackground(getSelectionBackground());
            c.setForeground(getSelectionForeground());
        }

        return c;
    }

    /**
     * Listens for debug window popup dialog events.
     */
    private class PopupListener extends MouseAdapter {
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
