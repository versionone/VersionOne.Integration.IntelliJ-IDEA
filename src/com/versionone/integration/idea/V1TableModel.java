package com.versionone.integration.idea;

import javax.swing.table.DefaultTableModel;

/**
 * Created by IntelliJ IDEA.
*/
public class V1TableModel extends DefaultTableModel {
    ColumnData[] columnData = null;

    public V1TableModel(Object[][] data, ColumnData[] columnData) {
        super(data, ColumnData.getTitleNames(columnData));
        this.columnData = columnData;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return columnData[column].isEditable();
    }
}
