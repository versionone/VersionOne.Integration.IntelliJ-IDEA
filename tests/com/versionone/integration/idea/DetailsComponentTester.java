package com.versionone.integration.idea;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.IDataLayer;

import javax.swing.*;
import java.awt.*;

public class DetailsComponentTester {

    public static void main(String[] args) throws DataLayerException {
        final WorkspaceSettings settings = new WorkspaceSettings();
        DetailsComponent plugin = new DetailsComponent(null, settings);
        final IDataLayer data = ApiDataLayer.getInstance();
        data.connect(settings.v1Path, settings.user, settings.passwd, settings.isWindowsIntegratedAuthentication);
        JPanel panel = plugin.createContentPanel(data);
        JFrame frame = new JFrame("IDEA V1 Plugin - Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(200, 500));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
