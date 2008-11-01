package com.versionone.integration.idea;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import java.util.EventObject;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 */
public class V1Table extends JTable {

    private JComboBox comboEditor = new JComboBox(new DataLayer().getAllStatuses());

    public V1Table(V1TableModel v1TableModel) {
        super(v1TableModel);
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int col) {
        if (col == 7) {
            comboEditor.setSelectedItem(DataLayer.getInstance().getValue(col, row));
            return new DefaultCellEditor(comboEditor);
        } else {
            return super.getCellEditor(row, col);
        }
    }
}
