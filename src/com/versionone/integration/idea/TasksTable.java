package com.versionone.integration.idea;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 */
public class TasksTable extends JTable {

    //private JComboBox comboEditor = new JComboBox(new DataLayer().getAllStatuses());
    private Set<Integer> rowsChanged = new HashSet<Integer>() ;
    //MVC-View cannot access DataLayer, must use MVC-Model 
    private DataLayer data = DataLayer.getInstance();

    public TasksTable(HorizontalTableModel v1TableModel) {
        super(v1TableModel);
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int col) {
        if (col == 7) {
            JComboBox comboEditor = new JComboBox(DataLayer.getInstance().getAllStatuses());
            //select current value
            //comboEditor.setSelectedItem(DataLayer.getInstance().getValue(col, row));
            ItemListener listener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    //DataLayer.getInstance().
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        System.out.println("row="+ row + " col="+col+"----"+e.getItem()+ " - "+e.paramString());
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
    public void tableChanged(TableModelEvent e) {
        super.tableChanged(e);
        System.out.println("e.getFirstRow()="+ e.getFirstRow());

//        HorizontalTableModel tabMod = (HorizontalTableModel)e.getSource();
//
//        if (TableModelEvent.HEADER_ROW != e.getFirstRow()) {
//            data.setNewTaskValue((tabMod).getColumn(e.getFirstRow()), e.getFirstRow());
//        }

        if (e.getFirstRow()>TableModelEvent.HEADER_ROW) {
            if (data.isTaskDataChanged(e.getFirstRow())) {
                rowsChanged.add(e.getFirstRow());
            }
            else {
                rowsChanged.remove(e.getFirstRow());
            }

        }
        repaint();
    }

    /*
    @Override
    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {

        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        if (getSelectedRow() != rowIndex) {
            if (rowsChanged.size() > 0 && rowsChanged.contains(rowIndex)) {
                c.setBackground(Color.yellow);
                c.setForeground(Color.black);
            } else {
                c.setBackground(getBackground());
                c.setForeground(getForeground());
            }
        }
        else
        {
                c.setBackground(getSelectionBackground());
                c.setForeground(getSelectionForeground());            
        }

        JTable.DropLocation dropLocation = getDropLocation();
        if (dropLocation != null) {
            System.out.println("rowIndex="+ rowIndex + " vColIndex="+vColIndex);
            System.out.println("dropLocation.getColumn()="+ dropLocation.getColumn() + " dropLocation.getRow()="+dropLocation.getRow());
        }

        return c;
    }
    */
}
