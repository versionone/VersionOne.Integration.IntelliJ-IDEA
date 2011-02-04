package com.versionone.common.sdk;

import com.versionone.Oid;
import com.versionone.apiclient.APIException;
import com.versionone.apiclient.AndFilterTerm;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.AssetState;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.ConnectionException;
import com.versionone.apiclient.FilterTerm;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IFilterTerm;
import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IOperation;
import com.versionone.apiclient.MetaException;
import com.versionone.apiclient.OrderBy;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.QueryResult;
import com.versionone.apiclient.V1Exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.versionone.common.sdk.EntityType.Defect;
import static com.versionone.common.sdk.EntityType.Scope;
import static com.versionone.common.sdk.EntityType.Story;

public class ApiDataLayer implements IDataLayer {

    private final VersionOneConnector connector = new VersionOneConnector();

    private static final Map<String, String> propertyAliases = new HashMap<String, String>();

    static {
        propertyAliases.put("DefectStatus", "StoryStatus");
        propertyAliases.put("DefectSource", "StorySource");
        propertyAliases.put("ScopeBuildProjects", "BuildProject");
        propertyAliases.put("TaskOwners", "Member");
        propertyAliases.put("StoryOwners", "Member");
        propertyAliases.put("DefectOwners", "Member");
        propertyAliases.put("TestOwners", "Member");
        propertyAliases.put("TaskScope", "Scope");
        propertyAliases.put("StoryScope", "Scope");
        propertyAliases.put("DefectScope", "Scope");
        propertyAliases.put("TestScope", "Scope");
    }

    /** Set of attributes to be queried in Workitem requests */
    private Set<AttributeInfo> attributesToQuery = new HashSet<AttributeInfo>();

    private final Map<EntityType, IAssetType> types = new HashMap<EntityType, IAssetType>(EntityType.values().length);
    /** All uncommitted Effort records */
    private final Map<Asset, Double> efforts = new HashMap<Asset, Double>();

    private IAssetType workitemType;
    private IAssetType primaryWorkitemType;
    private IAssetType effortType;

    protected static ApiDataLayer instance;

    Oid memberOid;

    private List<Asset> assetList;
    private Map<String, PropertyValues> listPropertyValues;

    private boolean trackEffort;
    public final EffortTrackingLevel trackingLevel = new EffortTrackingLevel();

    private String currentProjectId;
    private boolean showAllTasks = true;

    protected ApiDataLayer() {
        addProperty("Schedule.EarliestActiveTimebox", Scope, false);
    }

    public static ApiDataLayer getInstance() {
        if (instance == null) {
            instance = new ApiDataLayer();
        }
        return instance;
    }

    public void connect(String path, String userName, String password, boolean integrated) throws DataLayerException {
        final boolean credentialsChanged = connector.credentialsChanged(path, userName, integrated);

        assetList = null;

        try {
            connector.connect(path, userName, password, integrated);

            if (credentialsChanged) {
                cleanConnectionData();
            }
            if (types.isEmpty()) {
                initTypes();
            }
            processConfig();

            memberOid = connector.getServices().getLoggedIn();
            listPropertyValues = getListPropertyValues();
            //updateCurrentProjectId();
        } catch (MetaException ex) {
            throw createAndLogException("Cannot connect to V1 server.", ex);
        } catch (V1Exception ex) {
            throw createAndLogException("Cannot connect to V1 server.", ex);
        }
    }

    List<RequiredFieldsDTO> validate(Asset asset) {
        return connector.getRequiredFieldsValidator().validate(asset);
    }

    public boolean verifyConnection(ConnectionSettings settings) throws ConnectionException {
        return connector.verifyConnection(settings);
    }

    private void processConfig() throws ConnectionException, APIException {
        trackEffort = connector.getConfiguration().isEffortTracking();
        if (trackEffort) {
            effortType = connector.getMetaModel().getAssetType("Actual");
        }

        trackingLevel.clear();
        trackingLevel.addPrimaryTypeLevel(Story, connector.getConfiguration().getStoryTrackingLevel());
        trackingLevel.addPrimaryTypeLevel(Defect, connector.getConfiguration().getDefectTrackingLevel());
    }

    private void cleanConnectionData() {
        efforts.clear();
        types.clear();
        workitemType = null;
        primaryWorkitemType = null;
    }

    private void initTypes() {
        IMetaModel metaModel = connector.getMetaModel();
        for (EntityType type : EntityType.values()) {
            types.put(type, metaModel.getAssetType(type.name()));
        }
        workitemType = metaModel.getAssetType("Workitem");
        primaryWorkitemType = metaModel.getAssetType("PrimaryWorkitem");
    }

    /**
     * Reconnect with settings used in last connect() call.
     *
     * @throws DataLayerException
     */
    public void reconnect() throws DataLayerException {
        connect(connector.getPath(), connector.getUserName(), connector.getPassword(), connector.getIntegrated());
    }

    public List<Project> getProjectTree() throws DataLayerException {
        checkConnection();

        try {
            final IAssetType projectType = types.get(Scope);
            final Query scopeQuery = new Query(projectType, projectType.getAttributeDefinition("Parent"));
            final FilterTerm stateTerm = new FilterTerm(projectType.getAttributeDefinition("AssetState"));
            stateTerm.NotEqual(AssetState.Closed);
            scopeQuery.setFilter(stateTerm);
            // clear all definitions used in previous queries
            addSelection(scopeQuery, Scope);
            final QueryResult result = connector.getServices().retrieve(scopeQuery);
            final List<Project> roots = new ArrayList<Project>(result.getAssets().length);
            for (Asset oneAsset : result.getAssets()) {
                roots.add(new Project(this, oneAsset));
            }
            return roots;
        } catch (Exception ex) {
            throw createAndLogException("Can't get projects list.", ex);
        }
    }

    public List<PrimaryWorkitem> getWorkitemTree() throws DataLayerException {
        checkConnection();

        if (currentProjectId == null) {
            currentProjectId = getDefaultProjectId();
        }
        if (assetList == null) {
            assetList = queryWorkitemTree();
        }
        final List<PrimaryWorkitem> res = new ArrayList<PrimaryWorkitem>(assetList.size());
        for (Asset asset : assetList) {
            if (isDisplayed(asset)) {
                res.add(new PrimaryWorkitem(this, asset));
            }
        }
        return res;
    }

    public boolean isConnected() {
        return connector.isConnected();
    }

     void checkConnection() throws DataLayerException {
        if (!connector.isConnected()) {
            reconnect();
            if (!connector.isConnected()) {
                throw ApiDataLayer.createAndLogException("Connection is not set.");
            }
        }
    }

    private List<Asset> queryWorkitemTree() throws DataLayerException {
        try {
            IAttributeDefinition parentDef = workitemType.getAttributeDefinition("Parent");
            Query query = new Query(workitemType, parentDef);

            for (EntityType type : EntityType.values()) {
                if (type.isWorkitem()) {
                    addSelection(query, type);
                }
            }

            query.setFilter(getScopeFilter(workitemType));
            query.getOrderBy().majorSort(primaryWorkitemType.getDefaultOrderBy(), OrderBy.Order.Ascending);
            query.getOrderBy().minorSort(workitemType.getDefaultOrderBy(), OrderBy.Order.Ascending);

            final Asset[] assets = connector.getServices().retrieve(query).getAssets();
            final ArrayList<Asset> list = new ArrayList<Asset>(assets.length + 20);
            for (Asset asset : assets) {
                if (checkWorkitemIsValid(asset)) {
                    list.add(asset);
                }
            }
            return list;
        } catch (MetaException ex) {
            throw createAndLogException("Unable to get workitems.", ex);
        } catch (Exception ex) {
            throw createAndLogException("Unable to get workitems.", ex);
        }
    }

    /**
     * Sets visibility for workitems
     *
     * @param showAllTasks true - all workitems can be shown false - only changed, new
     *                     and workitem with current owner can be shown
     */
    public void setShowAllTasks(boolean showAllTasks) {
        this.showAllTasks = showAllTasks;
    }

    /**
     * Determines whether this Asset can be showed or not.
     *
     * @param asset to determine visibility status.
     * @return true if Asset can be showed at the moment; otherwise - false.
     */
    boolean isDisplayed(Asset asset) {
        if (showAllTasks || asset.hasChanged() || asset.getOid().isNull()) {
            return true;
        }

        final Attribute attribute = asset.getAttribute(workitemType.getAttributeDefinition(Workitem.OWNERS_PROPERTY));
        final Object[] owners = attribute.getValues();
        for (Object oid : owners) {
            if (memberOid.equals(oid)) {
                return true;
            }
        }

        for (Asset child : asset.getChildren()) {
            if (isDisplayed(child)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkWorkitemIsValid(Asset asset) {
        return (asset.getAssetType().getToken().equals(Workitem.STORY_NAME)
                || asset.getAssetType().getToken().equals(Workitem.DEFECT_NAME)
                || asset.getAssetType().getToken().equals(Workitem.TASK_NAME)
                || asset.getAssetType().getToken().equals(Workitem.TEST_NAME));
    }

    private IFilterTerm getScopeFilter(IAssetType assetType) {
        List<FilterTerm> terms = new LinkedList<FilterTerm>();
        FilterTerm term = new FilterTerm(assetType.getAttributeDefinition("Scope.AssetState"));
        term.NotEqual(AssetState.Closed);
        terms.add(term);
        term = new FilterTerm(assetType.getAttributeDefinition("Scope.ParentMeAndUp"));
        term.Equal(currentProjectId);
        terms.add(term);
        term = new FilterTerm(assetType.getAttributeDefinition("Timebox.State.Code"));
        term.Equal("ACTV");
        terms.add(term);
        term = new FilterTerm(assetType.getAttributeDefinition("AssetState"));
        term.NotEqual(AssetState.Closed);
        terms.add(term);
        return new AndFilterTerm(terms.toArray(new FilterTerm[terms.size()]));
    }

    // TODO refactor
    private void addSelection(Query query, EntityType type) throws DataLayerException {
        for (AttributeInfo attrInfo : attributesToQuery) {
            if (attrInfo.type == type) {
                try {
                    query.getSelection().add(types.get(attrInfo.type).getAttributeDefinition(attrInfo.attr));
                } catch (MetaException ex) {
                    logException("Wrong attribute: " + attrInfo, ex);
                }
            }
        }

        RequiredFieldsValidator validator = connector.getRequiredFieldsValidator();
        if (validator.getFields(type) == null) {
            return;
        }

        for (RequiredFieldsDTO field : validator.getFields(type)) {
            try {
                query.getSelection().add(types.get(type).getAttributeDefinition(field.name));
            } catch (MetaException ex) {
                logException("Wrong attribute: " + field.name, ex);
            }
        }
    }

    public void addProperty(String name, EntityType type, boolean isList) {
        attributesToQuery.add(new AttributeInfo(name, type, isList));
    }

    private Map<String, PropertyValues> getListPropertyValues() throws V1Exception, MetaException {
        Map<String, PropertyValues> res = new HashMap<String, PropertyValues>(attributesToQuery.size());
        for (AttributeInfo attrInfo : attributesToQuery) {
            if (!attrInfo.isList) {
                continue;
            }

            String propertyAlias = attrInfo.type + attrInfo.attr;
            if (!res.containsKey(propertyAlias)) {
                String propertyName = resolvePropertyKey(propertyAlias);

                PropertyValues values;
                if (res.containsKey(propertyName)) {
                    values = res.get(propertyName);
                } else {
                    values = queryPropertyValues(propertyName);
                    res.put(propertyName, values);
                }

                if (!res.containsKey(propertyAlias)) {
                    res.put(propertyAlias, values);
                }
            }
        }
        return res;
    }

    static String resolvePropertyKey(String propertyAlias) {
        if (propertyAliases.containsKey(propertyAlias)) {
            return propertyAliases.get(propertyAlias);
        }

        return propertyAlias;
    }

    PropertyValues queryPropertyValues(String propertyName) throws V1Exception, MetaException {
        IAssetType assetType = connector.getMetaModel().getAssetType(propertyName);
        IAttributeDefinition nameDef = assetType.getAttributeDefinition(Entity.NAME_PROPERTY);
        IAttributeDefinition inactiveDef = null;

        Query query = new Query(assetType);
        query.getSelection().add(nameDef);

        try {// Some properties may not have INACTIVE attribute
            inactiveDef = assetType.getAttributeDefinition("Inactive");
        } catch (MetaException ignore) {
        }
        if (inactiveDef != null) {
            FilterTerm filter = new FilterTerm(inactiveDef);
            filter.Equal("False");
            query.setFilter(filter);
        }

        query.getOrderBy().majorSort(assetType.getDefaultOrderBy(), OrderBy.Order.Ascending);

        final PropertyValues res = new PropertyValues();
        res.add(new ValueId());
        for (Asset asset : connector.getServices().retrieve(query).getAssets()) {
            String name = (String) asset.getAttribute(nameDef).getValue();
            res.add(new ValueId(asset.getOid(), name));
        }
        return res;
    }

    public PropertyValues getListPropertyValues(EntityType type, String propertyName) {
        String propertyKey = resolvePropertyKey(type + propertyName);
        return listPropertyValues.get(propertyKey);
    }

    static void logException(String message, Exception ex) {
        System.out.println(message);
        ex.printStackTrace();
    }

    // TODO refactor this. Why we create exceptions but do not throw them?
    static DataLayerException createAndLogException(String message, Exception ex) {
        logException(message, ex);
        return new DataLayerException(message, ex);
    }

    static DataLayerException createAndLogException(String message) {
        System.out.println(message);
        return new DataLayerException(message);
    }

    public boolean isTrackEffortEnabled() {
        return trackEffort;
    }

    Double getEffort(Asset asset) {
        return efforts.get(asset);
    }

    void setEffort(Asset asset, Double value) {
        if (value == null || value == 0) {
            efforts.remove(asset);
        } else {
            efforts.put(asset, value);
        }
    }

    void commitAsset(Asset asset) throws V1Exception {
        connector.getServices().save(asset);
        commitEffort(asset);
    }

    private void commitEffort(Asset asset) throws V1Exception {
        if (efforts.containsKey(asset)) {
            Asset effort = connector.getServices().createNew(effortType, asset.getOid());
            effort.setAttributeValue(effortType.getAttributeDefinition("Value"), efforts.get(asset));
            effort.setAttributeValue(effortType.getAttributeDefinition("Date"), new Date());
            connector.getServices().save(effort);
            efforts.remove(asset);
        }
    }

    void revertAsset(Asset asset) {
        asset.rejectChanges();
        efforts.remove(asset);
    }

    public boolean hasChanges() {
        try {
            for (PrimaryWorkitem pri : getWorkitemTree()) {
                if (pri.hasChanges()) {
                    return true;
                }
                for (SecondaryWorkitem sec : pri.children) {
                    if (sec.hasChanges()) {
                        return true;
                    }
                }
            }
            return false;
        } catch (DataLayerException ex) {
            //This means assets wasn't queried, so wasn't modified
            return false;
        }
    }

    public void commitChanges() throws DataLayerException, ValidatorException {
        checkConnection();
        commitWorkitemTree(getWorkitemTree());
    }

    public void commitWorkitemTree(List<PrimaryWorkitem> list) throws DataLayerException, ValidatorException {
        final Map<Asset, List<RequiredFieldsDTO>> requiredData = new HashMap<Asset, List<RequiredFieldsDTO>>();
        for (PrimaryWorkitem priItem : list) {
            final boolean committed = commitWorkitem(requiredData, priItem);
            if (committed || priItem.isPersistent()) {
                for (SecondaryWorkitem secItem : priItem.children) {
                    commitWorkitem(requiredData, secItem);
                }
            }
        }
        if (!requiredData.isEmpty()) {
            throw new ValidatorException(requiredData, this);
        }
    }

    private static boolean commitWorkitem(final Map<Asset, List<RequiredFieldsDTO>> requiredData, Workitem secItem)
            throws DataLayerException, ValidatorException {
        final List<RequiredFieldsDTO> secReq = secItem.validateRequiredFields();
        if (!secReq.isEmpty()) {
            requiredData.put(secItem.asset, secReq);
            return false;
        }
        secItem.commitChanges();
        return true;
    }

    void executeOperation(Asset asset, IOperation operation) throws V1Exception {
        connector.getServices().executeOperation(operation, asset.getOid());
    }

    /**
     * Update specified Workitem in cache from the server. Information about
     * children isn't queried and isn't updated.
     *
     * @param workitem to update
     * @return updated Asset of this Workitem.
     * @throws DataLayerException if there is a problem with getting workitems
     */
    Asset refreshWorkitem(Entity workitem) throws DataLayerException {
        try {
            final Asset oldAsset = workitem.asset;
            final IAttributeDefinition stateDef = oldAsset.getAssetType().getAttributeDefinition("AssetState");
            final Query query = new Query(oldAsset.getOid().getMomentless(), false);
            addSelection(query, workitem.getType());
            query.getSelection().add(stateDef);
            final Asset[] queryRes = connector.getServices().retrieve(query).getAssets();
            assert queryRes.length == 1;
            final Asset newAsset = queryRes[0];

            if (workitem instanceof SecondaryWorkitem) {
                final SecondaryWorkitem sw = (SecondaryWorkitem) workitem;
                Collections.replaceAll(sw.parent.asset.getChildren(), oldAsset, newAsset);
            } else {
                Collections.replaceAll(assetList, oldAsset, newAsset);
                newAsset.getChildren().addAll(oldAsset.getChildren());
            }
            return newAsset;
        } catch (MetaException ex) {
            throw createAndLogException("Unable to get workitems.", ex);
        } catch (Exception ex) {
            throw createAndLogException("Unable to get workitems.", ex);
        }
    }

    /**
     * Removes Workitem from Workitem cache. So on next getWorkitemTree call it
     * won't returned.
     *
     * @param item to remove.
     */
    void removeWorkitem(Workitem item) {
        if (item instanceof SecondaryWorkitem) {
            final SecondaryWorkitem sw = (SecondaryWorkitem) item;
            sw.parent.asset.getChildren().remove(item.asset);
        } else {
            assetList.remove(item.asset);
        }
    }

    public void setCurrentProjectId(String value) {
        if (projectExists(value)) {
            currentProjectId = value;
        } else {
            currentProjectId = getDefaultProjectId();
        }
        assetList = null;
    }

    public String getCurrentProjectId() {
        return currentProjectId;
    }

    public void setCurrentProject(Project value) {
        currentProjectId = value.getId();
        assetList = null;
    }

    public Project getCurrentProject() throws DataLayerException {
        if (currentProjectId == null || currentProjectId.equals("")) {
            currentProjectId = getDefaultProjectId();
        }
        return queryProject(currentProjectId);
    }

    public String getCurrentMemberToken() {
        return memberOid != null ? memberOid.getToken() : null;
    }

    private String getDefaultProjectId() {
        String id = "";

        Query query = new Query(types.get(Scope));

        QueryResult result = null;
        try {
            result = connector.getServices().retrieve(query);
        } catch (Exception ex) {
        }

        if (result != null && result.getTotalAvaliable() > 0) {
            id = result.getAssets()[0].getOid().getMomentless().getToken();
        }

        return id;
    }

    /**
     * Update current project Id to the root project Id from current server
     */
    public String updateCurrentProjectId() {
        currentProjectId = getDefaultProjectId();
        return currentProjectId;
    }

    private boolean projectExists(String id) {
        try {
            final Query query = new Query(Oid.fromToken(id, connector.getMetaModel()));
            addSelection(query, Scope);
            final QueryResult result = connector.getServices().retrieve(query);
            return result.getTotalAvaliable() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    private Project queryProject(String id) throws DataLayerException {
        if (!connector.isConnected() || id == null || id.equals("")) {
            return null;
        }

        try {
            final Query query = new Query(Oid.fromToken(id, connector.getMetaModel()));
            addSelection(query, Scope);
            final Asset[] result = connector.getServices().retrieve(query).getAssets();
            assert result.length == 1;
            return new Project(this, result[0]);
        } catch (MetaException ex) {
            connector.setDisconnected();
            throw createAndLogException("Unable to get projects", ex);
        } catch (Exception ex) {
            throw createAndLogException("Unable to get projects", ex);
        }
    }

    public String localizerResolve(String key) {
        return connector.getLocalizer().resolve(key);
    }

    /**
     * Creates new Story or Defect.
     *
     * @param type of ne Workitem.
     * @return newly created Workitem.
     * @throws DataLayerException       if any error.
     * @throws IllegalArgumentException when prefix or parent isn't a Workitem, or trying to create a
     *                                  wrong Workitem hierarchy.
     */
    public PrimaryWorkitem createNewPrimaryWorkitem(EntityType type) throws DataLayerException {
        WorkitemFactory factory = new WorkitemFactory(this, currentProjectId, attributesToQuery);
        PrimaryWorkitem item = factory.createNewPrimaryWorkitem(type, types.get(type));
        assetList.add(item.asset);
        return item;
    }

    public SecondaryWorkitem createNewSecondaryWorkitem(EntityType type, PrimaryWorkitem parent)
            throws DataLayerException {
        WorkitemFactory factory = new WorkitemFactory(this, currentProjectId, attributesToQuery);
        return factory.createNewSecondaryWorkitem(type, types.get(type), parent);
    }

    /**
     * Set or ensure Asset attribute value.
     *
     * @param value of the attribute; if null or Oid.Null then attribute will be just ensured.
     * @param attrName attribute name
     * @param asset item which attribute is to be modified
     * @throws MetaException if something wrong with attribute name.
     * @throws APIException  if something wrong with attribute setting/ensuring.
     */
    static void setAssetAttribute(final Asset asset, final String attrName, final Object value) throws MetaException,
            APIException {
        final IAssetType type = asset.getAssetType();
        IAttributeDefinition def = type.getAttributeDefinition(attrName);
        if (value == null || (value instanceof Oid && ((Oid) value).isNull())) {
            asset.ensureAttribute(def);
        } else {
            asset.setAttributeValue(def, value);
        }
    }
}