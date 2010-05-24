/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.IDataLayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ConfigForm implements UnnamedConfigurable {
    private JTextField serverUrl;
    private JTextField userName;
    private JTextField password;
    private JButton validateConnectionButton;
    private JPanel generalPanel;
    private JCheckBox windowsIntegratedAuthentication;
    private JCheckBox enableCheckBox;
    private WorkspaceSettings settings;
    private final IDataLayer dataLayer;
    private final TasksComponent tc;
    private boolean isConnectionCorrect = true;
    private boolean isConnectionVerified = true;

    public ConfigForm(WorkspaceSettings settings, Project project) {
        validateConnectionButton.setEnabled(false);
        enableConfiguration(settings.isEnable);

        this.settings = settings;
        tc = project.getComponent(TasksComponent.class);
        dataLayer = tc.getDataLayer();

        validateConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verifyConnection();
            }
        });

        KeyAdapter keyListener = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                verifyChanges();
            }
        };

        ItemListener itemListener = new ItemListener() {
          public void itemStateChanged(ItemEvent itemEvent) {
            int state = itemEvent.getStateChange();
            enableWindowsIntegratedAuthentication(state == ItemEvent.SELECTED);
            verifyChanges();
          }
        };

        ItemListener enableItemListener = new ItemListener() {
          public void itemStateChanged(ItemEvent itemEvent) {
            int state = itemEvent.getStateChange();
            enableConfiguration(state == ItemEvent.SELECTED);
            verifyChanges();
          }
        };

        enableCheckBox.addItemListener(enableItemListener);
        windowsIntegratedAuthentication.addItemListener(itemListener);
        serverUrl.addKeyListener(keyListener);
        userName.addKeyListener(keyListener);
        password.addKeyListener(keyListener);

        reset();
    }

    public JComponent createComponent() {
        //N/A
        return null;
    }

    public boolean isModified() {
        boolean result;

        result = enableCheckBox.isSelected() != settings.isEnable;
        result = result ||  windowsIntegratedAuthentication.isSelected() != settings.isWindowsIntegratedAuthentication;
        result = result || !serverUrl.getText().equals(settings.v1Path);
        result = result || !userName.getText().equals(settings.user);
        result = result || !password.getText().equals(settings.passwd);

        return result;
    }

    public void apply() throws ConfigurationException {
        if (isModified()) {
            settings.isEnable = enableCheckBox.isSelected();
            settings.v1Path = serverUrl.getText();
            settings.isWindowsIntegratedAuthentication = windowsIntegratedAuthentication.isSelected();
            if (settings.isWindowsIntegratedAuthentication) {
                settings.user = "";
                settings.passwd = "";
            } else {
                settings.user = userName.getText();
                settings.passwd = password.getText();
            }

            if (settings.isEnable) {
                tc.registerToolWindow();
                try {
                    dataLayer.connect(settings.v1Path, settings.user, settings.passwd,
                                      settings.isWindowsIntegratedAuthentication);
                    dataLayer.setCurrentProjectId(settings.projectToken);
                    settings.projectToken = dataLayer.getCurrentProjectId();
                    settings.projectName = com.versionone.common.sdk.Project.getNameById(dataLayer.getProjectTree(),
                                                                                         settings.projectToken);
                } catch (DataLayerException ex) {
                    Icon icon = Messages.getErrorIcon();
                    Messages.showMessageDialog(ex.getMessage(), "Error", icon);
                }
                tc.refresh();
                tc.update();
            } else {
                tc.unregisterToolWindow();
            }            
        }
    }

    public void reset() {
        serverUrl.setText(settings.v1Path);
        userName.setText(settings.user);
        password.setText(settings.passwd);
        windowsIntegratedAuthentication.setSelected(settings.isWindowsIntegratedAuthentication);
        enableCheckBox.setSelected(settings.isEnable);
    }

    public void disposeUIResources() {
        //Do nothing
    }

    protected JPanel getPanel() {
        return generalPanel;
    }

    // TODO join the following methods and possibly reduce fields
    protected boolean isConnectValid() {
        return isConnectionCorrect;
    }

    protected boolean isConnectVerified() {
        return isConnectionVerified;
    }

    private void verifyConnection() {
        String pathVersionOne = serverUrl.getText();
        if (pathVersionOne.length() != 0 && !pathVersionOne.endsWith("/")) {
            serverUrl.setText(pathVersionOne + "/");
        }

        isConnectionCorrect = dataLayer.verifyConnection(serverUrl.getText(), userName.getText(), password.getText(),
                windowsIntegratedAuthentication.isSelected());
        if (isConnectionCorrect) {
            Messages.showInfoMessage("Connection is valid", "Connection Status");
            isConnectionCorrect = true;
            validateConnectionButton.setEnabled(false);
        } else {
            Messages.showWarningDialog("Connection is invalid", "Connection Status");
            isConnectionCorrect = false;
        }
        isConnectionVerified = true;
    }

    private void verifyChanges() {
        if (isModified() && enableCheckBox.isSelected()) {
            isConnectionVerified = false;
            validateConnectionButton.setEnabled(true);
        }
    }

    private void enableConfiguration(boolean isEnable) {
        if (isEnable) {
            windowsIntegratedAuthentication.setEnabled(true);
            enableWindowsIntegratedAuthentication(windowsIntegratedAuthentication.isSelected());
            serverUrl.setEditable(true);
            validateConnectionButton.setEnabled(true);
        } else {
            windowsIntegratedAuthentication.setEnabled(false);
            serverUrl.setEditable(false);
            userName.setEditable(false);
            password.setEditable(false);
            validateConnectionButton.setEnabled(false);
        }
    }

    private void enableWindowsIntegratedAuthentication(boolean isIntegratedAuth) {
        if (isIntegratedAuth) {
            userName.setText("");
            password.setText("");
            userName.setEditable(false);
            password.setEditable(false);
        } else {
            userName.setEditable(true);
            password.setEditable(true);
        }
    }
}
