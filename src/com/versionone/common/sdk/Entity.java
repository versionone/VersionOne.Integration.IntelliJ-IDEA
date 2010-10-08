package com.versionone.common.sdk;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.versionone.Oid;
import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.MetaException;
import com.versionone.apiclient.V1Exception;
import com.versionone.apiclient.IAttributeDefinition.AttributeType;

public abstract class Entity {

    public static final String ID_PROPERTY = "Number";
    public static final String NAME_PROPERTY = "Name";

    public static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    static {
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
    }

    protected final ApiDataLayer dataLayer;
    final Asset asset;

    Entity(ApiDataLayer dataLayer, Asset asset) {
        this.asset = asset;
        this.dataLayer = dataLayer;
    }

    public EntityType getType() {
        return EntityType.valueOf(asset.getAssetType().getToken());
    }

    public String getId() {
        return asset.getOid().getMomentless().getToken();
    }

    public boolean hasChanges() {
        double effort = 0;
        String effortString = (String) getProperty(Workitem.EFFORT_PROPERTY);

        if(effortString != null) {
            try {
                effort = numberFormat.parse(effortString).doubleValue();
            } catch(ParseException ex) {
                // do nothing
            }
        }

        return !isPersistent() || asset.hasChanged() || effort != 0;
    }

    public boolean isPropertyReadOnly(String propertyName) {
        final String fullName = getType() + "." + propertyName;
        Attribute attribute = asset.getAttributes().get(fullName);
        if (attribute != null)
            return attribute.getDefinition().isReadOnly();
        else {
            ApiDataLayer.createAndLogException("Cannot get property: " + fullName);
            return true;
        }
    }

    private PropertyValues getPropertyValues(String propertyName) {
        return dataLayer.getListPropertyValues(getType(), propertyName);
    }

    /**
     * Checks if property value has changed.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     * @return true if property has changed; false - otherwise.
     * @throws IllegalArgumentException if property does not exist
     */
    public boolean isPropertyChanged(String propertyName) throws IllegalArgumentException {
        final String fullName = getType() + "." + propertyName;
        Attribute attribute = asset.getAttributes().get(fullName);
        if (attribute == null) {
            throw new IllegalArgumentException("There is no property: " + fullName);
        }
        return attribute.hasChanged();
    }

    /**
     * Resets property value if it was changed.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     * @throws IllegalArgumentException  If property does not exist
     */
    public void resetProperty(String propertyName) throws IllegalArgumentException {
        final String fullName = getType() + "." + propertyName;
        Attribute attribute = asset.getAttributes().get(fullName);
        if (attribute == null) {
            throw new IllegalArgumentException("There is no property: " + fullName);
        }
        attribute.rejectChanges();
    }

    /**
     * Gets property value.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     * @return String, ValueId or PropertyValues.
     * @throws IllegalArgumentException
     *             If property cannot be got or there is no such one.
     * @see #NAME_PROPERTY
     * @see Workitem#STATUS_PROPERTY
     * @see Workitem#DONE_PROPERTY
     * @see Workitem#SCHEDULE_NAME_PROPERTY
     * @see Workitem#OWNERS_PROPERTY
     * @see Workitem#TODO_PROPERTY
     */
    public Object getProperty(String propertyName) throws IllegalArgumentException {
        final String fullName = getType() + "." + propertyName;
        Attribute attribute = asset.getAttributes().get(fullName);

        if (attribute == null) {
            throw new IllegalArgumentException("There is no property: " + fullName);
        }

        final PropertyValues allValues = getPropertyValues(propertyName);
        if (attribute.getDefinition().isMultiValue()) {
            final Object[] currentValues = attribute.getValues();
            return allValues == null ? currentValues : allValues.subset(currentValues);
        }

        try {
            final Object val = attribute.getValue();
            if (val instanceof Oid) {
                return allValues == null ? val : allValues.find((Oid) val);
            } else if (val instanceof Double) {
                return numberFormat.format(((Double) val).doubleValue());
            }
            return val;
        } catch (APIException e) {
            throw new IllegalArgumentException("Cannot get property: " + propertyName, e);
        }
    }

    public String getPropertyAsString(String propertyName) throws IllegalArgumentException {
        Object value = getProperty(propertyName);
        return value == null ? "" : value.toString();
    }

    /**
     * Sets property value.
     * 
     * @param propertyName
     *            Short name of the property to set, e.g. "Name".
     * @param newValue
     *            String, Double, null, ValueId, PropertyValues accepted.
     */
    public void setProperty(String propertyName, Object newValue) {
        try {
            if ("".equals(newValue)) {
                newValue = null;
            }

            if (isNumeric(propertyName)) {
                setNumericProperty(propertyName, newValue);
            } else if (isMultivalue(propertyName)) {
                setMultiValueProperty(propertyName, (PropertyValues) newValue);
            } else {// List & String types
                if (newValue instanceof ValueId) {
                    newValue = ((ValueId) newValue).oid;
                }
                setPropertyInternal(propertyName, newValue);
            }
        } catch (APIException ex) {
            ApiDataLayer.logException("Cannot set property " + propertyName + " of " + this, ex);
        } catch (ParseException ex) {
            ApiDataLayer.logException("Cannot set property " + propertyName + " of " + this, ex);
        }
    }

    protected boolean isMultivalue(String propertyName) {
        try {
            final IAttributeDefinition attrDef = asset.getAssetType().getAttributeDefinition(propertyName);
            return attrDef.isMultiValue();
        } catch (MetaException e) {
            return false;
        }
    }

    protected boolean isNumeric(String propertyName) {
        try {
            final IAttributeDefinition attrDef = asset.getAssetType().getAttributeDefinition(propertyName);
            return attrDef.getAttributeType() == AttributeType.Numeric;
        } catch (MetaException e) {
            return false;
        }
    }

    protected void setNumericProperty(String propertyName, Object newValue) throws APIException, ParseException {
        final Double doubleValue;
        if (newValue instanceof String) {
            doubleValue = numberFormat.parse((String) newValue).doubleValue();
        } else if (newValue instanceof Double) {
            doubleValue = (Double) newValue;
        } else {
            throw new ParseException("Wrong newValue:" + newValue, -1);
        }

        if (doubleValue != null && doubleValue < 0 && propertyName != Workitem.EFFORT_PROPERTY) {
            throw new ParseException("The field cannot be negative", -1);
        }
        setPropertyInternal(propertyName, doubleValue);
    }

    protected void setPropertyInternal(String propertyName, Object newValue) throws APIException {
        final Attribute attribute = asset.getAttributes().get(getType() + "." + propertyName);
        if (attribute == null || !areEqual(attribute.getValue(), newValue)) {
            asset.setAttributeValue(asset.getAssetType().getAttributeDefinition(propertyName), newValue);
        }
    }

    protected static boolean areEqual(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    protected void setMultiValueProperty(String propertyName, PropertyValues newValues) throws APIException {
        final Attribute attribute = asset.getAttributes().get(getType() + "." + propertyName);
        final Object[] oldValues = attribute.getValues();
        final IAttributeDefinition attrDef = asset.getAssetType().getAttributeDefinition(propertyName);
        for (Object oldOid : oldValues) {
            if (!newValues.containsOid((Oid) oldOid)) {
                asset.removeAttributeValue(attrDef, oldOid);
            }
        }
        for (ValueId newValue : newValues) {
            if (!checkContains(oldValues, newValue.oid) && !newValue.oid.isNull()) {
                asset.addAttributeValue(attrDef, newValue.oid);
            }
        }
    }

    protected boolean checkContains(Object[] array, Object value) {
        for (Object item : array) {
            if (item.equals(value))
                return true;
        }
        return false;
    }

    public void commitChanges() throws DataLayerException, ValidatorException {
        try {
            final List<RequiredFieldsDTO> req = validateRequiredFields();
            if (!req.isEmpty()) {
                Map<Asset, List<RequiredFieldsDTO>> requiredData = new HashMap<Asset, List<RequiredFieldsDTO>>();
                requiredData.put(asset, req);
                throw new ValidatorException(requiredData, dataLayer);
            }
            dataLayer.commitAsset(asset);
        } catch (V1Exception e) {
            throw ApiDataLayer.createAndLogException("Failed to commit changes of workitem: " + this, e);
        }
    }

    public List<RequiredFieldsDTO> validateRequiredFields() throws DataLayerException {
        return dataLayer.validate(asset);
    }

    protected void checkPersistance(String job) {
        if (!isPersistent()) {
            throw new UnsupportedOperationException("Cannot " + job + " non-saved workitem.");
        }
    }

    public void revertChanges() {
        checkPersistance("revertChanges");
        dataLayer.revertAsset(asset);
    }

    /**
     * Defines whether this workitem exist on server. Otherwise this workitem
     * created on client and still not committed to server.
     * 
     * @return true if this workitem was persisted to server; false - otherwise.
     */
    public boolean isPersistent() {
        return !asset.getOid().isNull();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity other = (Entity) obj;
        if (isPersistent()) {
            return other.asset.getOid().equals(asset.getOid());
        }
        return asset.equals(other.asset);
    }

    @Override
    public int hashCode() {
        return asset.getOid().hashCode();
    }

    @Override
    public String toString() {
        return (isPersistent() ? getId() : getType()) + (hasChanges() ? " (Changed)" : "");
    }
}