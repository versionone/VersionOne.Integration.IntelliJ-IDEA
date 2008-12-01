/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.versionone.common.sdk.IDataLayer;

import javax.swing.*;
import java.awt.*;
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
    private WorkspaceSettings settings;
    //private Project project;
    private final IDataLayer dataLayer;
    private final TasksComponent tc;
    private boolean isConnectionCorrect = true;
    private boolean isConnectionVerified = true;

    public ConfigForm(WorkspaceSettings settings, Project project) {
        validateConnectionButton.setEnabled(false);

        this.settings = settings;
        //this.project = project;
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
            if (state == ItemEvent.SELECTED) {
                userName.setText("");
                password.setText("");
                userName.setEditable(false);
                password.setEditable(false);
            } else {
                userName.setEditable(true);
                password.setEditable(true);
            }
            verifyChanges();
          }
        };

        windowsIntegratedAuthentication.addItemListener(itemListener);
        serverUrl.addKeyListener(keyListener);
        userName.addKeyListener(keyListener);
        password.addKeyListener(keyListener);


        reset();
    }

    private void verifyConnection() {
        isConnectionCorrect = dataLayer.isConnectionValid(serverUrl.getText(), userName.getText(), password.getText());
        if (isConnectionCorrect) {
            Messages.showInfoMessage("Connection is correct", "Connection status");
            isConnectionCorrect = true;
            validateConnectionButton.setEnabled(false);
        } else {
            Messages.showWarningDialog("Connection is not correct", "Connection status");
            isConnectionCorrect = false;
        }
        isConnectionVerified = true;
    }

    public Component getContentPanel() {
        return generalPanel;
    }

    public JComponent createComponent() {
        //N/A
        return null;
    }

    public boolean isModified() {
        boolean result;

        result = windowsIntegratedAuthentication.isSelected() != settings.isWindowsIntegratedAuthentication; 
        result = result || !serverUrl.getText().equals(settings.v1Path);
        result = result || !userName.getText().equals(settings.user);
        result = result || !password.getText().equals(settings.passwd);

        return result;
    }

    public void verifyChanges() {

        if (isModified()) {
            isConnectionVerified = false;
            validateConnectionButton.setEnabled(true);
        }
    }

//    public boolean isProjectViewStyleChanged() {
////        final RApplicationSettings settings = RApplicationSettings.getInstance();
////        return settings.useRubySpecificProjectView != useRubyProjectViewBox.isSelected();
//        return true;
//    }

    public void apply() throws ConfigurationException {

        if (isModified()) {
            settings.v1Path = serverUrl.getText();
            settings.isWindowsIntegratedAuthentication = windowsIntegratedAuthentication.isSelected();
            if (settings.isWindowsIntegratedAuthentication) {
                settings.user = "";
                settings.passwd = "";
            } else {
                settings.user = userName.getText();
                settings.passwd = password.getText();
            }

            settings.projectToken = "";
            settings.projectName = "";

            try {
                dataLayer.reconnect();
            } catch (V1PluginException e) {
                Icon icon = e.isError() ? Messages.getErrorIcon() : Messages.getWarningIcon();
                Messages.showMessageDialog(e.getMessage(), "Error", icon);
            }

            tc.update();
        }

//        final RApplicationSettings settings = RApplicationSettings.getInstance();
//        settings.useConsoleOutputOtherFilters = otherFiltersCheckBox.isSelected();
//        settings.useConsoleOutputRubyStacktraceFilter = rubyStacktraceFilterCheckBox.isSelected();
//        settings.useConsoleColorMode = colorModeCheckBox.isSelected();
//        settings.additionalEnvPATH = myTFAdditioanlPath.getText().trim();
//
//        final boolean isProjectViewStyleChanged = isProjectViewStyleChanged();
//        settings.useRubySpecificProjectView = useRubyProjectViewBox.isSelected();
//        if (isProjectViewStyleChanged){
//            final int result = Messages.showYesNoDialog(myContentPane.getParent(),
//                    RBundle.message("settings.plugin.general.tab.use.ruby.dataLayer.view.changed.message"),
//                    RBundle.message("settings.plugin.general.tab.use.ruby.dataLayer.view.changed.title"),
//                    Messages.getQuestionIcon());
//            if (result == DialogWrapper.OK_EXIT_CODE){
//                ApplicationManager.getApplication().invokeLater(new Runnable() {
//                    public void run() {
//                        // reload all projects
//                        for (Project dataLayer : ProjectManager.getInstance().getOpenProjects()) {
//                            ProjectManager.getInstance().reloadProject(dataLayer);
//                        }
//                    }
//                }, ModalityState.NON_MODAL);
//            }
//        }
    }

    public boolean isConnectValid() {
        return isConnectionCorrect;
    }

    public boolean isConnectVerified() {
        return isConnectionVerified;
    }

    public void reset() {

        serverUrl.setText(settings.v1Path);
        userName.setText(settings.user);
        password.setText(settings.passwd);
        windowsIntegratedAuthentication.setSelected(settings.isWindowsIntegratedAuthentication);

//        final RApplicationSettings settings = RApplicationSettings.getInstance();
//        rubyStacktraceFilterCheckBox.setSelected(settings.useConsoleOutputRubyStacktraceFilter);
//        otherFiltersCheckBox.setSelected(settings.useConsoleOutputOtherFilters);
//        colorModeCheckBox.setSelected(settings.useConsoleColorMode);
//        useRubyProjectViewBox.setSelected(settings.useRubySpecificProjectView);
//        myTFAdditioanlPath.setText(settings.additionalEnvPATH);
//
//        myTPIdeaPath.setText(System.getenv(OSUtil.getPATHenvVariableName()));
    }

    public void disposeUIResources() {
        //Do nothing
    }

    public JPanel getPanel() {
        return generalPanel;
    }
}
