package com.versionone.integration.idea;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.IDataLayer;

import javax.swing.*;
import java.awt.*;

public class DetailsComponentRunner {

    public static void main(String[] args) throws Exception {
        Configuration config = Configuration.getInstance();
        config.fill();
        final WorkspaceSettings settings = new WorkspaceSettings();
        DetailsComponent plugin = new DetailsComponent(null, settings);
        final IDataLayer dataLayer = ApiDataLayer.getInstance();
        dataLayer.connect(settings.v1Path, settings.user, settings.passwd, settings.isWindowsIntegratedAuthentication);
        JPanel panel = plugin.createContentPanel();
        JFrame frame = new JFrame("IDEA V1 Plugin - Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(200, 500));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
