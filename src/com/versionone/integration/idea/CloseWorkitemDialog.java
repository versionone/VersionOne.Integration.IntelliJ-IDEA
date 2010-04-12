package com.versionone.integration.idea;

import com.versionone.common.sdk.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CloseWorkitemDialog extends JDialog {

    private final Workitem item;
    private final IDataLayer dataLayer;

    public CloseWorkitemDialog(JFrame parent, @NotNull Workitem item, @NotNull IDataLayer dataLayer) {
        super(parent, "Close " + item.getType().name(), true);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout());
        setSize(300, 110);
        setResizable(false);
        this.item = item;
        this.dataLayer = dataLayer;

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setSize(300, 55);

        JLabel toDoLabel = new JLabel("To Do");
        JTextField toDoText = new JTextField(10);
        toDoText.setEditable(false);
        bindToDoTextField(toDoText);

        JLabel statusLabel = new JLabel("Status");
        JComboBox statusComboBox = new JComboBox();
        bindStatusComboBox(statusComboBox);

        addComponentsToParent(topPanel, toDoLabel, toDoText, statusLabel, statusComboBox);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setSize(300, 55);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("should close item");
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        addComponentsToParent(bottomPanel, okButton, cancelButton);

        add(topPanel);
        add(bottomPanel);

        okButton.requestFocusInWindow();
    }

    private void bindStatusComboBox(JComboBox comboBox) {
        PropertyValues statuses = dataLayer.getListPropertyValues(item.getType(), Workitem.STATUS_PROPERTY);
        for(ValueId status : statuses) {
            comboBox.addItem(status);
        }

        ValueId status = (ValueId) item.getProperty(Workitem.STATUS_PROPERTY);
        comboBox.setSelectedIndex(statuses.getStringArrayIndex(status));
    }

    private void bindToDoTextField(JTextField toDoField) {
        Object toDo = item.getProperty(Workitem.TODO_PROPERTY);
        if(toDo != null) {
            toDoField.setText((String) toDo);
        }
    }

    private void addComponentsToParent(JComponent parent, JComponent... components) {
        for(JComponent component : components) {
            parent.add(component);
        }
    }
}
