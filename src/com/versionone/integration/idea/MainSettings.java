/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MainSettings implements ApplicationComponent, Configurable {
    private ConfigForm form;
    private WorkspaceSettings settings;

    public MainSettings(Project project,  WorkspaceSettings settings) {
        this.settings = settings;
    }

    public void initComponent() {
        //loadRegisteredData();
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "Settings";
    }

    public String getDisplayName() {
        return "Version One";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (form == null) {
            form = new ConfigForm(settings);
        }
        return form.getPanel();
    }

    public boolean isModified() {
        return form == null || form.isModified();
    }

    public void apply() throws ConfigurationException {
        if (form != null) {
            // Get data from form to component
            form.apply();
        }
    }

    public void reset() {
        if (form != null) {
            // Reset form data from component
            form.reset();
        }
    }

    public void disposeUIResources() {
        form = null;
    }

//    private void loadRegisteredData() {
//        final RubyShortcutsSettings settings = RubyShortcutsSettings.getInstance();
//        if (settings.serializableGenerators != null) {
//            loadGenerator(settings.serializableGenerators);
//        }
//
//        if (settings.serializableRakeTask != null) {
//            loadRakeTask(settings.serializableRakeTask);
//        }
//
//        //Register default actions in dedault keymap
//        final Keymap[] keymaps = KeymapManagerEx.getInstanceEx().getAllKeymaps();
//        for (Keymap keymap : keymaps) {
//            if (KeymapManager.DEFAULT_IDEA_KEYMAP.equals(keymap.getName())) {
//                registerDefaultActions(keymap);
//            }
//        }
//    }

//    private void registerDefaultActions(final Keymap keymap) {
//        //Generators
//        final KeyStroke ctrAltG = KeyStroke.getKeyStroke("ctrl alt G");
//        keymap.addShortcut(GeneratorNodeInfo.getActionId("controller"),
//                           new KeyboardShortcut(ctrAltG, KeyStroke.getKeyStroke("C")));
//        keymap.addShortcut(GeneratorNodeInfo.getActionId("model"),
//                           new KeyboardShortcut(ctrAltG, KeyStroke.getKeyStroke("M")));
//        keymap.addShortcut(GeneratorNodeInfo.getActionId("migration"),
//                           new KeyboardShortcut(ctrAltG, KeyStroke.getKeyStroke("I")));
//        keymap.addShortcut(GeneratorNodeInfo.getActionId("scaffold"),
//                           new KeyboardShortcut(ctrAltG, KeyStroke.getKeyStroke("S")));
//
//        //RakeTasks
//        final KeyStroke ctrAltR = KeyStroke.getKeyStroke("ctrl alt R");
//        keymap.addShortcut(RakeTaskNodeInfo.getActionId("db:migrate"),
//                           new KeyboardShortcut(ctrAltR, KeyStroke.getKeyStroke("I")));
//    }

//    private void loadRakeTask(final RakeTask task) {
//        if (!task.isGroup()) {
//            final String cmd = task.getFullCommand();
//            assert cmd != null;
//            final String actionId = RakeTaskNodeInfo.getActionId(task.getFullCommand());
//            new ShortcutAction(task.getId(),
//                               cmd, RailsIcons.RAKE_TASK_ICON,
//                               ShortcutsTreeState.RAKE_SUBTREE).registerInKeyMap(actionId);
//            return;
//        }
//        final List<? extends RakeTask> children = task.getSubTasks();
//        for (RakeTask child : children) {
//            loadRakeTask(child);
//        }
//    }
//
//    private void loadGenerator(final SerializableGenerator generator) {
//        if (!generator.isGroup()) {
//            final String actionId = GeneratorNodeInfo.getActionId(generator.getName());
//            new ShortcutAction(generator.getName(),
//                                   generator.getName(),
//                                   RailsIcons.GENERATOR_ICON,
//                                   ShortcutsTreeState.GENERATORS_SUBTREE).registerInKeyMap(actionId);
//            return;
//        }
//        final List<SerializableGenerator> children = generator.getChildren();
//        for (SerializableGenerator child : children) {
//            loadGenerator(child);
//        }
//    }
}

