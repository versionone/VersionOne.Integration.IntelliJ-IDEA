package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.Workitem;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CloseWorkitemDialog extends JDialog {

    private final Workitem item;
    private final IDataLayer dataLayer;

    public CloseWorkitemDialog(@NotNull Workitem item, @NotNull IDataLayer dataLayer) {
        super((JFrame) null, "Close " + item.getType().name(), true);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout());
        setSize(300, 110);
        setResizable(false);
        this.item = item;
        this.dataLayer = dataLayer;

        JLabel toDoLabel = new JLabel("To Do");
        JTextField toDoText = new JTextField(10);

        JLabel statusLabel = new JLabel("Status");
        JComboBox statusComboBox = new JComboBox();
        fillStatusComboBox(statusComboBox);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO commit status changes and close item
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        addComponents(toDoLabel, toDoText, statusLabel, statusComboBox, okButton, cancelButton);

    }

    private void fillStatusComboBox(JComboBox comboBox) {
        PropertyValues statuses = dataLayer.getListPropertyValues(item.getType(), Workitem.STATUS_PROPERTY);
        for(ValueId status : statuses) {
            comboBox.addItem(status);
        }
    }

    private void addComponents(JComponent... components) {
        for(JComponent component : components) {
            add(component);
        }
    }
}
