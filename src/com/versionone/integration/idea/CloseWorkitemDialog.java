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

    private final JComboBox statusComboBox;
    private final JTextField toDoText;

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
        toDoText = new JTextField(10);
        toDoText.setEditable(false);
        bindToDoTextField();

        JLabel statusLabel = new JLabel("Status");
        statusComboBox = new JComboBox();
        bindStatusComboBox();

        addComponentsToParent(topPanel, toDoLabel, toDoText, statusLabel, statusComboBox);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setSize(300, 55);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeClose();
                setVisible(false);
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

        centerDialog();
    }

    private void centerDialog() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height)/ 2);
    }

    private void executeClose() {
        try {
            ValueId currentStatus = (ValueId) item.getProperty(Workitem.STATUS_PROPERTY);
            ValueId selectedStatus = (ValueId) statusComboBox.getSelectedItem();
            if(!currentStatus.equals(selectedStatus)) {
                item.setProperty(Workitem.STATUS_PROPERTY, selectedStatus);
                item.commitChanges();
            }
            item.close();
        } catch(DataLayerException ex) {
            showFailureMessage(ex.getMessage());
        } catch(ValidatorException ex) {
            showFailureMessage(ex.getMessage());
        }
    }

    private void showFailureMessage(String message) {
        Icon icon = com.intellij.openapi.ui.Messages.getErrorIcon();
        com.intellij.openapi.ui.Messages.showMessageDialog(message, "Error", icon);
    }

    private void bindStatusComboBox() {
        PropertyValues statuses = dataLayer.getListPropertyValues(item.getType(), Workitem.STATUS_PROPERTY);
        for(ValueId status : statuses) {
            statusComboBox.addItem(status);
        }

        ValueId status = (ValueId) item.getProperty(Workitem.STATUS_PROPERTY);
        statusComboBox.setSelectedItem(status);
    }

    private void bindToDoTextField() {
        Object toDo = item.getProperty(Workitem.TODO_PROPERTY);
        if(toDo != null) {
            toDoText.setText((String) toDo);
        }
    }

    private void addComponentsToParent(JComponent parent, JComponent... components) {
        for(JComponent component : components) {
            parent.add(component);
        }
    }
}
