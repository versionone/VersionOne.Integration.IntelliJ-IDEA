package com.versionone.integration.idea;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.ConnectionSettings;
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
        dataLayer.connect(getConnectionSettings(settings));
        JPanel panel = plugin.createContentPanel();
        JFrame frame = new JFrame("IDEA V1 Plugin - Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(200, 500));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private static ConnectionSettings getConnectionSettings(WorkspaceSettings settings) {
        ConnectionSettings connectionSettings = new ConnectionSettings();
        connectionSettings.v1Path = settings.v1Path;
        connectionSettings.v1Username = settings.user;
        connectionSettings.v1Password = settings.passwd;
        connectionSettings.isWindowsIntegratedAuthentication = settings.isWindowsIntegratedAuthentication;
        connectionSettings.isProxyEnabled = settings.isProxyEnabled;
        connectionSettings.proxyPassword = settings.proxyPassword;
        connectionSettings.proxyUri = settings.proxyUri;
        connectionSettings.proxyUsername = settings.proxyUsername;

        return connectionSettings;
    }
}
