/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.ui.Messages;
import com.versionone.apiclient.ConnectionException;
import com.versionone.common.sdk.ConnectionSettings;
import com.versionone.common.sdk.IDataLayer;
import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private JCheckBox chkUseProxy;
    private JLabel lblProxyUri;
    private JLabel lblProxyUsername;
    private JLabel lblProxyPassword;
    private JTextField txtProxyUri;
    private JTextField txtProxyUsername;
    private JTextField txtProxyPassword;
    private boolean isConnectionCorrect = true;
    private boolean isConnectionVerified = true;

    private WorkspaceSettings settings;
    private final IDataLayer dataLayer;    

    public ConfigForm(WorkspaceSettings settings, IDataLayer dataLayer) {
        this.settings = settings;
        this.dataLayer = dataLayer;

        validateConnectionButton.setEnabled(false);
        enableConfiguration(settings.isEnabled);
        registerListeners();
        registerKeyAdapters();
        reset();
    }

    public JComponent createComponent() {
        //N/A
        return null;
    }

    public boolean isModified() {
        boolean result;

        result = enableCheckBox.isSelected() != settings.isEnabled;
        result = result ||  windowsIntegratedAuthentication.isSelected() != settings.isWindowsIntegratedAuthentication;
        result = result || !serverUrl.getText().equals(settings.v1Path);
        result = result || !userName.getText().equals(settings.user);
        result = result || !password.getText().equals(settings.passwd);
        result = result || !txtProxyPassword.getText().equals(settings.proxyPassword);
        result = result || !txtProxyUri.getText().equals(settings.proxyUri);
        result = result || !txtProxyUsername.getText().equals(settings.proxyUsername);
        result = result || chkUseProxy.isSelected() != settings.isProxyEnabled;

        return result;
    }

    public void apply() throws ConfigurationException {
        if (isModified()) {
            settings.isEnabled = enableCheckBox.isSelected();
            settings.v1Path = serverUrl.getText();
            settings.isWindowsIntegratedAuthentication = windowsIntegratedAuthentication.isSelected();
            settings.isProxyEnabled = chkUseProxy.isSelected();
            settings.proxyUri = txtProxyUri.getText();
            settings.proxyUsername = txtProxyUsername.getText();
            settings.proxyPassword = txtProxyPassword.getText();
            if (settings.isWindowsIntegratedAuthentication) {
                settings.user = "";
                settings.passwd = "";
            } else {
                settings.user = userName.getText();
                settings.passwd = password.getText();
            }
        }
    }

    public void reset() {
        serverUrl.setText(settings.v1Path);
        userName.setText(settings.user);
        password.setText(settings.passwd);
        windowsIntegratedAuthentication.setSelected(settings.isWindowsIntegratedAuthentication);
        enableCheckBox.setSelected(settings.isEnabled);
        chkUseProxy.setSelected(settings.isProxyEnabled);
        txtProxyPassword.setText(settings.proxyPassword);
        txtProxyUri.setText(settings.proxyUri);
        txtProxyUsername.setText(settings.proxyUsername);
    }

    public void disposeUIResources() {}

    protected JPanel getPanel() {
        return generalPanel;
    }

    protected boolean isConnectionVerified() {
        return isConnectionCorrect && isConnectionVerified;
    }

    private void verifyConnection() {
        String pathVersionOne = serverUrl.getText();
        if (pathVersionOne.length() != 0 && !pathVersionOne.endsWith("/")) {
            serverUrl.setText(pathVersionOne + "/");
        }
        try {
            isConnectionCorrect = dataLayer.verifyConnection(getSettings());
            if (isConnectionCorrect) {
                Messages.showInfoMessage("Connection is valid", "Connection Status");
                isConnectionCorrect = true;
                validateConnectionButton.setEnabled(false);
            } else {
                Messages.showWarningDialog("Connection is invalid", "Connection Status");
                isConnectionCorrect = false;
            }
        } catch (ConnectionException ex) {
            Messages.showWarningDialog(ex.getMessage(), "Connection Status");
            isConnectionCorrect = false;
        }
        isConnectionVerified = true;
    }

    private ConnectionSettings getSettings() {
        ConnectionSettings settings = new ConnectionSettings();
        settings.v1Path = serverUrl.getText();
        settings.v1Username = userName.getText();
        settings.v1Password = password.getText();
        settings.isWindowsIntegratedAuthentication = windowsIntegratedAuthentication.isSelected();
        settings.isProxyEnabled = chkUseProxy.isSelected();
        settings.proxyUri = txtProxyUri.getText();
        settings.proxyUsername = txtProxyUsername.getText();
        settings.proxyPassword = txtProxyPassword.getText();
        return settings;
    }

    private void registerListeners() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verifyConnection();
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

        ItemListener useProxyListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                int state = itemEvent.getStateChange();
                enableProxyUsing(state == ItemEvent.SELECTED && enableCheckBox.isSelected());
                verifyChanges();
            }
        };

        validateConnectionButton.addActionListener(actionListener);
        enableCheckBox.addItemListener(enableItemListener);
        windowsIntegratedAuthentication.addItemListener(itemListener);
        chkUseProxy.addItemListener(useProxyListener);
    }

    private void registerKeyAdapters() {
        KeyAdapter keyListener = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                verifyChanges();
            }
        };

        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                verifyChanges();
            }
        };

        serverUrl.addKeyListener(keyListener);
        userName.addKeyListener(keyListener);
        password.addKeyListener(keyListener);
        chkUseProxy.addChangeListener(changeListener);
        windowsIntegratedAuthentication.addChangeListener(changeListener);
        txtProxyUsername.addKeyListener(keyListener);
        txtProxyPassword.addKeyListener(keyListener);
        txtProxyUri.addKeyListener(keyListener);
    }

    private void verifyChanges() {
        if (isModified() && enableCheckBox.isSelected()) {
            isConnectionVerified = false;
            validateConnectionButton.setEnabled(true);
        }
    }

    private void enableConfiguration(boolean isEnable) {
        if (isEnable) {
            enableWindowsIntegratedAuthentication(windowsIntegratedAuthentication.isSelected());
            enableProxyUsing(chkUseProxy.isSelected());
        } else {
            userName.setEditable(false);
            password.setEditable(false);
            txtProxyPassword.setEditable(false);
            txtProxyUri.setEditable(false);
            txtProxyUsername.setEditable(false);
        }
        windowsIntegratedAuthentication.setEnabled(isEnable);
        chkUseProxy.setEnabled(isEnable);
        serverUrl.setEditable(isEnable);
        validateConnectionButton.setEnabled(isEnable);
    }

    private void enableWindowsIntegratedAuthentication(boolean isIntegratedAuth) {
        if (isIntegratedAuth) {
            userName.setText("");
            password.setText("");
        }
        userName.setEditable(!isIntegratedAuth);
        password.setEditable(!isIntegratedAuth);
    }

    private void enableProxyUsing(boolean useProxy) {
        txtProxyPassword.setEditable(useProxy);
        txtProxyUri.setEditable(useProxy);
        txtProxyUsername.setEditable(useProxy);
    }

    public boolean isPluginEnabled() {
        return enableCheckBox.isSelected();
    }
}
