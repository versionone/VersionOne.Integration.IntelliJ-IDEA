package com.versionone.integration.idea;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.Table;
import com.versionone.apiclient.V1Exception;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.ConnectException;

/**
 * Created by IntelliJ IDEA.
 */
public class TasksTable extends Table {

    private final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();

    public TasksTable(HorizontalTableModel v1TableModel) {
        super(v1TableModel);
    }

    @Override
    public HorizontalTableModel getModel() {
        return (HorizontalTableModel) super.getModel();
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int col) {
        if (col == 7) {
            JComboBox comboEditor = null;
            comboEditor = new JComboBox(DataLayer.getInstance().getAllStatuses());
            //select current value
            //comboEditor.setSelectedItem(DataLayer.getInstance().getValue(col, row));
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

        JTable.DropLocation dropLocation = getDropLocation();
        if (dropLocation != null) {
            System.out.println("rowIndex=" + rowIndex + " vColIndex=" + vColIndex);
            System.out.println("dropLocation.getColumn()=" + dropLocation.getColumn() + " dropLocation.getRow()=" + dropLocation.getRow());
        }

        return c;
    }
}
