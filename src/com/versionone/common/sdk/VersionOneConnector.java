package com.versionone.common.sdk;

import com.versionone.apiclient.*;

public class VersionOneConnector {

    private static final String META_SUFFIX = "meta.v1/";
    private static final String LOCALIZER_SUFFIX = "loc.v1/";
    private static final String DATA_SUFFIX = "rest-1.v1/";
    private static final String CONFIG_SUFFIX = "config.v1/";

    private String path;
    private String userName;
    private String password;
    private boolean integrated;

    private IMetaModel metaModel;
    private IServices services;
    private ILocalizer localizer;
    private V1Configuration config;

    private boolean isConnected;

    private RequiredFieldsValidator requiredFieldsValidator;

    RequiredFieldsValidator getRequiredFieldsValidator() {
        return requiredFieldsValidator;
    }

    String getPath() {
        return path;
    }

    String getUserName() {
        return userName;
    }

    String getPassword() {
        return password;
    }

    boolean getIntegrated() {
        return integrated;
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

    void connect(String path, String userName, String password, boolean integrated) throws DataLayerException {
        final boolean credentialsChanged = credentialsChanged(path, userName, integrated);

        this.path = path;
        this.userName = userName;
        this.password = password;
        this.integrated = integrated;

        try {
            if (credentialsChanged) {
                V1APIConnector metaConnector = new V1APIConnector(path + META_SUFFIX, userName, password);
                metaModel = new MetaModel(metaConnector);

                V1APIConnector localizerConnector = new V1APIConnector(path + LOCALIZER_SUFFIX, userName, password);
                localizer = new Localizer(localizerConnector);

                V1APIConnector dataConnector = new V1APIConnector(path + DATA_SUFFIX, userName, password);
                services = new Services(metaModel, dataConnector);

                isConnected = verifyConnection(metaModel, metaConnector);
            }

            config = new V1Configuration(new V1APIConnector(path + CONFIG_SUFFIX));

            requiredFieldsValidator = new RequiredFieldsValidator(metaModel, services);
            requiredFieldsValidator.init();
        } catch (MetaException ex) {
            isConnected = false;
            throw ApiDataLayer.createAndLogException("Cannot connect to VersionOne server.", ex);
        }
    }

    void reconnect() throws DataLayerException {
        connect(path, userName, password, integrated);
    }

     boolean verifyConnection(String url, String user, String pass, boolean integratedAuth) {
        V1APIConnector metaConnector = new V1APIConnector(url + META_SUFFIX);
        MetaModel model = new MetaModel(metaConnector);

        final V1APIConnector dataConnector;
        if (integratedAuth) {
            dataConnector = new V1APIConnector(url + DATA_SUFFIX);
        } else {
            dataConnector = new V1APIConnector(url + DATA_SUFFIX, user, pass);
        }

         return verifyConnection(model, dataConnector);
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

    // TODO Need refactor by Mikhail
    boolean credentialsChanged(String path, String userName, boolean integrated) {
        boolean isUserChanged = true;
        if ((this.userName != null || integrated) && this.path != null) {
            isUserChanged = (this.userName != null && !this.userName.equals(userName)) || integrated != this.integrated
                    || !this.path.equals(path);
        }
        return isUserChanged || metaModel == null || localizer == null || services == null;
    }
}
