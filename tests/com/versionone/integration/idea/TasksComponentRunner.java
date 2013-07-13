package com.versionone.integration.idea;

import javax.swing.*;
import java.awt.*;

public class TasksComponentRunner {

    public static void main(String[] args) {
        Configuration config = Configuration.getInstance();
        config.fill();
        TasksComponent plugin = new TasksComponent(null, new WorkspaceSettings());
        JPanel panel = plugin.createContentPanel();
        JFrame frame = new JFrame("IDEA V1 Plugin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 100));
        frame.add(panel);
        frame.pack();
        frame.show();
        frame.setVisible(true);
    }
}
