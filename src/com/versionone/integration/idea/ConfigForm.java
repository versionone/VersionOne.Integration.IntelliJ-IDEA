/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConfigForm implements UnnamedConfigurable {
    private JTextField serverUrl;
    private JTextField textField1;
    private JTextField textField2;
    private JButton validateConnectionButton;
    private JPanel generalPanel;
    private WorkspaceSettings settings;

    public ConfigForm(WorkspaceSettings settings) {
        validateConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Messages.showWarningDialog("Button pressed", e.paramString());
            }
        });
        validateConnectionButton.setEnabled(false);

        this.settings = settings;

        reset();
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

        result = !serverUrl.getText().equals(settings.v1Path);
        result = result || !textField1.getText().equals(settings.user);
        result = result || !textField2.getText().equals(settings.passwd);

        validateConnectionButton.setEnabled(result);


        return result;
    }

//    public boolean isProjectViewStyleChanged() {
////        final RApplicationSettings settings = RApplicationSettings.getInstance();
////        return settings.useRubySpecificProjectView != useRubyProjectViewBox.isSelected();
//        return true;
//    }

    public void apply() throws ConfigurationException {

        settings.v1Path = serverUrl.getText();
        settings.user = textField1.getText();
        settings.passwd = textField2.getText();

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
//                    RBundle.message("settings.plugin.general.tab.use.ruby.project.view.changed.message"),
//                    RBundle.message("settings.plugin.general.tab.use.ruby.project.view.changed.title"),
//                    Messages.getQuestionIcon());
//            if (result == DialogWrapper.OK_EXIT_CODE){
//                ApplicationManager.getApplication().invokeLater(new Runnable() {
//                    public void run() {
//                        // reload all projects
//                        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
//                            ProjectManager.getInstance().reloadProject(project);
//                        }
//                    }
//                }, ModalityState.NON_MODAL);
//            }
//        }
    }

    public void reset() {

        serverUrl.setText(settings.v1Path);
        textField1.setText(settings.user);
        textField2.setText(settings.passwd);

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
