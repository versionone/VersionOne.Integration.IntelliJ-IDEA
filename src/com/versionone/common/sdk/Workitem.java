package com.versionone.common.sdk;

import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.V1Exception;

public abstract class Workitem extends Entity {

	public static final String STORY_NAME = "Story";
    public static final String DEFECT_NAME = "Defect";
    public static final String TASK_NAME = "Task";
    public static final String TEST_NAME = "Test";
	
	public static final String OWNERS_PROPERTY = "Owners";
    public static final String DETAIL_ESTIMATE_PROPERTY = "DetailEstimate";
    public static final String STATUS_PROPERTY = "Status";
    public static final String TYPE_PROPERTY = "Category";
    public static final String EFFORT_PROPERTY = "Actuals";
    public static final String DONE_PROPERTY = "Actuals.Value.@Sum";
    public static final String SCHEDULE_NAME_PROPERTY = "Schedule.Name";
    public static final String TODO_PROPERTY = "ToDo";
    public static final String ESTIMATE_PROPERTY = "Estimate";
    public static final String DESCRIPTION_PROPERTY = "Description";
    public static final String PARENT_NAME_PROPERTY = "Parent.Name";
    public static final String SCOPE_NAME_PROPERTY = "Scope.Name";

    public static final String CHECK_SIGNUP_PROPERTY = "CheckQuickSignup";
    public static final String CHECK_QUICK_CLOSE_PROPERTY = "CheckQuickClose";

    public static final String OP_SIGNUP = "QuickSignup";
    public static final String OP_CLOSE = "Inactivate";
    public static final String OP_QUICK_CLOSE = "QuickClose";

    Workitem(ApiDataLayer dataLayer, Asset asset) {
        super(dataLayer, asset);
    }

    public boolean isPropertyReadOnly(String propertyName) {
        if (isEffortTrackingRelated(propertyName) && !dataLayer.trackingLevel.isTracking(this)) {
            return true;
        }
        if (propertyName.equals(Workitem.EFFORT_PROPERTY)) {
            return false;
        }
        return super.isPropertyReadOnly(propertyName);
    }

    private boolean isEffortTrackingRelated(String property) {
        return property.startsWith("Actuals") || property.equals(Workitem.DETAIL_ESTIMATE_PROPERTY)
                || property.equals(Workitem.TODO_PROPERTY);
    }

    /**
     * Checks if property value has changed.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     * @return true if property has changed; false - otherwise.
     */
    public boolean isPropertyChanged(String propertyName) throws IllegalArgumentException {
        if (propertyName.equals(Workitem.EFFORT_PROPERTY)) {
            return dataLayer.getEffort(asset) != null;
        }
        return super.isPropertyChanged(propertyName);
    }

    /**
     * Resets property value if it was changed.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     */
    public void resetProperty(String propertyName) throws IllegalArgumentException {
        if (propertyName.equals(Workitem.EFFORT_PROPERTY)) {
            dataLayer.setEffort(asset, null);
        } else {
            super.resetProperty(propertyName);
        }
    }

    /**
     * Gets property value.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     * @return String, ValueId or PropertyValues.
     * @throws IllegalArgumentException
     *             If property cannot be got or there is no such one.
     * @see Workitem#EFFORT_PROPERTY
     * @see Entity.getProperty(String)
     */
    public Object getProperty(String propertyName) throws IllegalArgumentException {
        if (propertyName.equals(Workitem.EFFORT_PROPERTY)) {
            final Double effort = dataLayer.getEffort(asset);
            return effort == null ? null : numberFormat.format(effort.doubleValue());
        }
        return super.getProperty(propertyName);
    }

    protected boolean isNumeric(String propertyName) {
        if (propertyName.equals(Workitem.EFFORT_PROPERTY)) {
            return true;
        }
        return super.isNumeric(propertyName);
    }

    protected void setPropertyInternal(String propertyName, Object newValue) throws APIException {
        if (propertyName.equals(Workitem.EFFORT_PROPERTY)) {
            dataLayer.setEffort(asset, (Double) newValue);
        } else {
            super.setPropertyInternal(propertyName, newValue);
        }
    }

    public boolean canQuickClose() {
        try {
            return isPersistent() && (Boolean) getProperty(CHECK_QUICK_CLOSE_PROPERTY);
        } catch (IllegalArgumentException e) {
            ApiDataLayer.createAndLogException("QuickClose not supported.", e);
            return false;
        } catch (NullPointerException e) {
            ApiDataLayer.createAndLogException("QuickClose not supported.", e);
            return false;
        }
    }

    /**
     * Performs 'QuickClose' operation.
     * 
     * @throws DataLayerException
     */
    public void quickClose() throws DataLayerException, ValidatorException {
        checkPersistance("quickClose");
        commitChanges();
        try {
            dataLayer.executeOperation(asset, asset.getAssetType().getOperation(Workitem.OP_QUICK_CLOSE));
            dataLayer.removeWorkitem(this);
        } catch (V1Exception e) {
            throw ApiDataLayer.createAndLogException("Failed to QuickClose workitem: " + this, e);
        }
    }

    public boolean canSignup() {
        try {
            return isPersistent() && (Boolean) getProperty(CHECK_SIGNUP_PROPERTY);
        } catch (IllegalArgumentException e) {
            ApiDataLayer.createAndLogException("QuickSignup not supported.", e);
            return false;
        } catch (NullPointerException e) {
            ApiDataLayer.createAndLogException("QuickClose not supported.", e);
            return false;
        }
    }

    /**
     * Performs 'QuickSignup' operation.
     * 
     * @throws DataLayerException
     */
    public void signup() throws DataLayerException {
        checkPersistance("signup");
        try {
            dataLayer.executeOperation(asset, asset.getAssetType().getOperation(Workitem.OP_SIGNUP));
            dataLayer.refreshWorkitem(this);
        } catch (V1Exception e) {
            throw ApiDataLayer.createAndLogException("Failed to QuickSignup workitem: " + this, e);
        }
    }

    /**
     * Perform 'Inactivate' operation.
     * 
     * @throws DataLayerException
     */
    public void close() throws DataLayerException {
        checkPersistance("close");
        try {
            dataLayer.executeOperation(asset, asset.getAssetType().getOperation(Workitem.OP_CLOSE));
            dataLayer.removeWorkitem(this);
        } catch (V1Exception e) {
            throw ApiDataLayer.createAndLogException("Failed to Close workitem: " + this, e);
        }
    }

    public boolean isMine() {
        final PropertyValues owners = (PropertyValues) getProperty(Workitem.OWNERS_PROPERTY);
        return owners.containsOid(dataLayer.memberOid);
    }
}
