package com.versionone.common.sdk;

import com.versionone.apiclient.*;
import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

import java.net.URI;
import java.net.URISyntaxException;

public class VersionOneConnector {

    private static final String META_SUFFIX = "meta.v1/";
    private static final String LOCALIZER_SUFFIX = "loc.v1/";
    private static final String DATA_SUFFIX = "rest-1.v1/";
    private static final String CONFIG_SUFFIX = "config.v1/";

    private ConnectionSettings connectionSettings;

    private IMetaModel metaModel;
    private IServices services;
    private ILocalizer localizer;
    private V1Configuration config;

    private boolean isConnected;

    private RequiredFieldsValidator requiredFieldsValidator;

    RequiredFieldsValidator getRequiredFieldsValidator() {
        return requiredFieldsValidator;
    }

    boolean isConnected() {
        return isConnected;
    }

    void setDisconnected() {
        isConnected = false;
    }

    IMetaModel getMetaModel() {
        return metaModel;
    }

    IServices getServices() {
        return services;
    }

    ILocalizer getLocalizer() {
        return localizer;
    }

    V1Configuration getConfiguration() {
        return config;
    }

    //void connect(String path, String userName, String password, boolean integrated) throws DataLayerException {
    void connect(ConnectionSettings connectionSettings) throws DataLayerException {
        final String path = connectionSettings.v1Path;
        final String username = connectionSettings.v1Username;
        final String password = connectionSettings.v1Password;
        final boolean integratedAuth = connectionSettings.isWindowsIntegratedAuthentication;
        final boolean credentialsChanged = credentialsChanged(connectionSettings);
        ProxyProvider proxyProvider = null;
        try {
            proxyProvider = getProxy(connectionSettings);
        } catch (URISyntaxException e) {

        }

        this.connectionSettings = connectionSettings;

        try {
            if (credentialsChanged) {
                V1APIConnector metaConnector = new V1APIConnector(path + META_SUFFIX, username, password, proxyProvider);
                metaModel = new MetaModel(metaConnector);

                V1APIConnector localizerConnector = new V1APIConnector(path + LOCALIZER_SUFFIX, username, password, proxyProvider);
                localizer = new Localizer(localizerConnector);

                V1APIConnector dataConnector = new V1APIConnector(path + DATA_SUFFIX, username, password, proxyProvider);
                services = new Services(metaModel, dataConnector);

                isConnected = verifyConnection(metaModel, dataConnector);
            }

            config = new V1Configuration(new V1APIConnector(path + CONFIG_SUFFIX, null, null, proxyProvider));

            requiredFieldsValidator = new RequiredFieldsValidator(metaModel, services);
            requiredFieldsValidator.init();
        } catch (MetaException ex) {
            isConnected = false;
            throw ApiDataLayer.createAndLogException("Cannot connect to VersionOne server.", ex);
        }
    }

    void reconnect() throws DataLayerException {
        connect(connectionSettings);
    }

    boolean verifyConnection(ConnectionSettings settings) throws ConnectionException {
        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        ProxyProvider proxy;
        try {
            proxy = getProxy(settings);
        } catch (URISyntaxException ex) {
            throw new ConnectionException("Proxy Uri is not correct", ex);
        }
        V1APIConnector metaConnector = new V1APIConnector(settings.v1Path + META_SUFFIX, null, null, proxy);
        MetaModel model = new MetaModel(metaConnector);

        final V1APIConnector dataConnector;

        if (settings.isWindowsIntegratedAuthentication) {
            dataConnector = new V1APIConnector(settings.v1Path + DATA_SUFFIX, null, null, proxy);
        } else {
            dataConnector = new V1APIConnector(settings.v1Path + DATA_SUFFIX, settings.v1Username, settings.v1Password, proxy);
        }

        return verifyConnection(model, dataConnector);
    }

    private ProxyProvider getProxy(ConnectionSettings settings) throws URISyntaxException {
        if (!settings.isProxyEnabled) {
            return null;
        }
        URI uri = new URI(settings.proxyUri);
        return new ProxyProvider(uri, settings.proxyUsername, settings.proxyPassword);
    }

    private boolean verifyConnection(IMetaModel model, V1APIConnector dataConnector) {
        Services v1Service = new Services(model, dataConnector);

        try {
            v1Service.getLoggedIn();
        } catch (V1Exception ex) {
            return false;
        } catch (MetaException ex) {
            return false;
        }
        return true;
    }

    boolean credentialsChanged(ConnectionSettings newSettings) {
        if (!isConnectionInitialized()) {
            return true;
        }
        String currentUsername = connectionSettings.v1Username;
        String currentPath = connectionSettings.v1Path;
        if (isProxySettingsChanged(newSettings)) {
            return true;
        }
        if (currentUsername != null || newSettings.isWindowsIntegratedAuthentication) {
            return isUserChanged(newSettings.v1Username, newSettings.isWindowsIntegratedAuthentication) || !currentPath.equals(newSettings.v1Path);
        }
        return true;
    }

    private boolean isProxySettingsChanged(ConnectionSettings newSettings) {
        return newSettings.isProxyEnabled != connectionSettings.isProxyEnabled ||
                (newSettings.proxyPassword != null && !newSettings.proxyPassword.equals(connectionSettings.proxyPassword)) ||
                (newSettings.proxyUri != null && !newSettings.proxyUri.equals(connectionSettings.proxyUri)) ||
                (newSettings.proxyUsername != null && !newSettings.proxyUsername.equals(connectionSettings.proxyUsername));
    }

    private boolean isUserChanged(String username, boolean integrated) {
        return (connectionSettings.v1Username != null && !connectionSettings.v1Username.equals(username))
                || integrated != connectionSettings.isWindowsIntegratedAuthentication;
    }

    private boolean isConnectionInitialized() {
        return metaModel != null && localizer != null && services != null &&
                connectionSettings != null && connectionSettings.v1Path != null;
    }
}
