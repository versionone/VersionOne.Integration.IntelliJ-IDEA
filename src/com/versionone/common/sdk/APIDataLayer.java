/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.sdk;

import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.AssetState;
import com.versionone.apiclient.FilterTerm;
import com.versionone.apiclient.IAPIConnector;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IServices;
import com.versionone.apiclient.MetaException;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.OrderBy;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.QueryResult;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1APIConnector;
import com.versionone.apiclient.V1Configuration;
import com.versionone.apiclient.V1Exception;
import com.versionone.integration.idea.V1PluginException;
import com.versionone.integration.idea.WorkspaceSettings;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class requests, stores data from VersionOne server and send changed data back.
 */
public final class APIDataLayer implements IDataLayer {

    private static final Logger LOG = Logger.getLogger(APIDataLayer.class);
    private static final String META_URL_SUFFIX = "meta.v1/";
    private static final String DATA_URL_SUFFIX = "rest-1.v1/";
    private static final String CONFIG_URL_SUFFIX = "config.v1/";

    // APIClient objects
    private IServices services = null;
    private IMetaModel metaModel = null;
    private IAssetType actualType = null;

    private final WorkspaceSettings cfg;

    private String member;
//    private ListTypeValues statusList;
//    private ListTypeValues typesList;
//    private ListTypeValues sourcesList;

    private boolean trackEffort = false;

    private Task[] taskList = new Task[0];

    private static String ERROR_CONNECTION_TO_V1 = "Error connection to VersionOne";

    private boolean isConnectSet = false;

    public APIDataLayer(WorkspaceSettings workspaceSettings) {
        cfg = workspaceSettings;

        try {
            connect();
            refresh();
        } catch (V1PluginException e) {
            // do nothing
        }
    }

    /**
     *
     */
    private void connect() throws V1PluginException {
        isConnectSet = false;
        try {
            V1APIConnector metaConnector = new V1APIConnector(cfg.v1Path + META_URL_SUFFIX);
            metaModel = new MetaModel(metaConnector);

            V1APIConnector dataConnector = new V1APIConnector(cfg.v1Path + DATA_URL_SUFFIX, cfg.user, cfg.passwd);
            services = new Services(metaModel, dataConnector);

            V1Configuration v1Config = new V1Configuration(new V1APIConnector(cfg.v1Path + CONFIG_URL_SUFFIX));

            actualType = metaModel.getAssetType("Actual");
            trackEffort = v1Config.isEffortTracking();
            member = services.getLoggedIn().getToken();
        } catch (Exception e) {
            LOG.warn(ERROR_CONNECTION_TO_V1, e);
            throw new V1PluginException(ERROR_CONNECTION_TO_V1, e, true);
        }
        isConnectSet = true;
    }

    public void refresh() throws V1PluginException {
        System.out.println("DataLayer.refresh() prj=" + cfg.projectName);
        taskList = new Task[0];

        if (!isConnectSet) {
            try {
                connect();
            } catch (V1PluginException e) {
                throw new V1PluginException(ERROR_CONNECTION_TO_V1, e, true);
            }
        }

        if (!isConnectionValid(cfg.v1Path, cfg.user, cfg.passwd)) {
            throw new V1PluginException(ERROR_CONNECTION_TO_V1, true);
        } else if (cfg.projectToken.equals("")) {
            throw new V1PluginException("Project is not selected. Please use filter for set it.", false);
        }

        try {
            TasksProperties.reloadListValues(metaModel, services);
            taskList = getTasks();
        } catch (Exception e) {
            LOG.warn(ERROR_CONNECTION_TO_V1, e);
            throw new V1PluginException(ERROR_CONNECTION_TO_V1, e, true);
        }
    }

    /**
     * Get Task Assigned to this user.
     *
     * @return Array of Task assigned to this user.
     * @throws V1Exception
     */
    private Task[] getTasks() throws Exception {
        IAssetType taskType = metaModel.getAssetType("Task");

        Query query = new Query(taskType);
        addTaskSelection(query, taskType);
        addFilter(query, taskType);

        OrderBy order = new OrderBy();
        order.majorSort(taskType.getAttributeDefinition("Parent.Order"), OrderBy.Order.Ascending);
        order.minorSort(taskType.getAttributeDefinition("Order"), OrderBy.Order.Ascending);
        query.setOrderBy(order);

        QueryResult result = services.retrieve(query);
        Asset[] taskAssets = result.getAssets();

        Task[] rc = new Task[taskAssets.length];
        for (int i = 0; i < taskAssets.length; ++i) {
            rc[i] = new Task(taskAssets[i]);
        }

        return rc;
    }

    private static void addTaskSelection(Query query, IAssetType taskType) throws MetaException {
        for (String oneAttribute : TasksProperties.getAllAttributes()) {
            query.getSelection().add(taskType.getAttributeDefinition(oneAttribute));
        }
    }

    private void addFilter(Query query, IAssetType taskType) throws MetaException {
        FilterTerm[] terms = new FilterTerm[5];
        terms[0] = new FilterTerm(taskType.getAttributeDefinition("Scope.AssetState"), FilterTerm.Operator.NotEqual, AssetState.Closed);
        terms[1] = new FilterTerm(taskType.getAttributeDefinition("Scope.ParentMeAndUp"), FilterTerm.Operator.Equal, cfg.projectToken);
        if (!cfg.isShowAllTask) {
            terms[2] = new FilterTerm(taskType.getAttributeDefinition("Owners"), FilterTerm.Operator.Equal, member);
        }
        terms[3] = new FilterTerm(taskType.getAttributeDefinition("Timebox.State.Code"), FilterTerm.Operator.Equal, "ACTV");
        terms[4] = new FilterTerm(taskType.getAttributeDefinition("AssetState"), FilterTerm.Operator.NotEqual, AssetState.Closed);
        query.setFilter(Query.and(terms));
    }


    /**
     * @throws IllegalStateException if trying to commit Efforts when EffortTracking disabled.
     */
    public void commitChangedTaskData() throws Exception {
        if (!isConnectSet) {
            try {
                connect();
            } catch (V1PluginException e) {
                throw new V1PluginException(ERROR_CONNECTION_TO_V1, e, true);
            }
        }
        if (!isConnectionValid(cfg.v1Path, cfg.user, cfg.passwd)) {
            throw new V1PluginException(ERROR_CONNECTION_TO_V1, true);
        } else if (cfg.projectToken.equals("")) {
            throw new V1PluginException("Project is not selected. Please use filter for set it.", false);
        }
        synchronized (taskList) {
            save(taskList);
        }
    }

    private void save(Task[] tasks) throws Exception {
        Asset[] assets = new Asset[tasks.length];
        int i = 0;
        for (Task oneTask : tasks) {
            final BigDecimal effortValue = (BigDecimal) oneTask.getProperty(TasksProperties.EFFORT);
            if (effortValue.compareTo(BigDecimal.ZERO) == 1) {
                Asset effort = services.createNew(actualType, oneTask.asset.getOid());
                effort.setAttributeValue(actualType.getAttributeDefinition("Value"), effortValue);
                effort.setAttributeValue(actualType.getAttributeDefinition("Date"), new Date());
                oneTask.asset.getNewAssets().put("Actuals", effort);
            }
            assets[i++] = oneTask.asset;
        }
        this.services.save(assets);
    }

    public boolean isTaskChanged() {
        boolean result = false;
        for (Task oneTask : taskList) {
            if (oneTask.isChanged()) {
                result = true;
                break;
            }
        }

        return result;
    }

    public int getTasksCount() {
        synchronized (taskList) {
            return taskList.length;
        }
    }

    public String getTaskPropertyValue(int task, TasksProperties property) {
        //TODO redesign
        if (taskList.length == 0) {
            return null;
        }
        Object value;
        synchronized (taskList) {
            value = taskList[task].getProperty(property);
        }
        if (value == null) {
            return null;
        }
        String res = value.toString();
        if (value instanceof Oid) {
            Oid oid = (Oid) value;
            if (!oid.isNull()) {
                if (property.type == TasksProperties.Type.LIST) {
                    res = property.getValueName(oid);
                }
            } else {
                res = null;
            }
        } else if (value instanceof Double) {
            res = BigDecimal.valueOf((Double) value).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        } else {
            res = value.toString();
        }
        return res;
    }

    public boolean isTrackEffort() {
        return trackEffort;
    }

    public void setTaskPropertyValue(int task, TasksProperties property, String value) {
        synchronized (taskList) {
            taskList[task].setProperty(property, value);
        }
    }

    @NotNull
    public ProjectTreeNode getProjects() throws V1PluginException {
        if (!isConnectSet) {
            try {
                connect();
            } catch (V1PluginException e) {
                throw new V1PluginException(ERROR_CONNECTION_TO_V1, e, true);
            }
        }
        if (!isConnectionValid(cfg.v1Path, cfg.user, cfg.passwd)) {
            throw new V1PluginException(ERROR_CONNECTION_TO_V1, true);
        }

        try {
            IAssetType scopeType = metaModel.getAssetType("Scope");

            IAttributeDefinition scopeName = scopeType.getAttributeDefinition("Name");

            Query scopeQuery = new Query(scopeType, scopeType.getAttributeDefinition("Parent"));

            FilterTerm stateTerm = Query.term(scopeType.getAttributeDefinition("AssetState"));
            stateTerm.NotEqual(AssetState.Closed);

            scopeQuery.setFilter(stateTerm);
            scopeQuery.getSelection().add(scopeName);

            QueryResult result = services.retrieve(scopeQuery);

            List<Asset> assets = Arrays.asList(result.getAssets());

            ProjectTreeNode root = new ProjectTreeNode();
            recurseAndAddNodes(root.children, assets, scopeName);
            return root;
        } catch (Exception e) {
            LOG.warn("Can't get projects list.", e);
            throw new V1PluginException("Can't get projects list.", e, false);
        }
    }

    private void recurseAndAddNodes(List<ProjectTreeNode> projectTreeNodes, List<Asset> assets, IAttributeDefinition scopeName) throws V1Exception {
        int i = 0;
        for (Asset oneAsset : assets) {
            ProjectTreeNode oneNode = new ProjectTreeNode(
                    (String) oneAsset.getAttribute(scopeName).getValue(),
                    oneAsset.getOid().getToken(),
                    null, i++);
            projectTreeNodes.add(oneNode);
            recurseAndAddNodes(oneNode.children, oneAsset.getChildren(), scopeName);
        }
    }

    public boolean isTaskDataChanged(int task) {
        return taskList[task].isChanged();
    }

    public boolean isPropertyChanged(int task, TasksProperties property) {
        return taskList[task].isPropertyChanged(property);
    }

    public void reconnect() throws V1PluginException {
        connect();
        refresh();
    }

    public boolean isConnectionValid(String path, String userName, String password) {
        boolean result = true;

        IAPIConnector connector = new V1APIConnector(path + DATA_URL_SUFFIX + "Data/StoryStatus", userName, password);
        try {
            connector.getData().close();
        } catch (Exception e) {
            result = false;
        }

        return result;
    }
}
