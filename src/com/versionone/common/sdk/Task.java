package com.versionone.common.sdk;

import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.MetaException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents one Task in the VersionOne system
 */
class Task {

    private static final String TASK_PREFIX = "Task.";

    public final Asset asset;
    private BigDecimal effort = BigDecimal.ZERO.setScale(2);
    private Map<String, IAttributeDefinition> definitions = new HashMap<String, IAttributeDefinition>(TasksProperties.values().length);

    /**
     * Create
     *
     * @param asset - Task asset
     */
    public Task(Asset asset) throws MetaException {
        this.asset = asset;
    }

    public String getToken() throws Exception {
        return asset.getOid().getToken();
    }

    /**
     * Get the value of an attribute
     *
     * @param key - name of attribute
     */
    private Object getValue(String key) {
        Object value = null;
        try {
            value = asset.getAttributes().get(TASK_PREFIX + key).getValue();
        } catch (Exception e) {
            //do nothing
        }
        return value;
    }

    public void setProperty(TasksProperties property, Object value) {
        if (property.equals(TasksProperties.EFFORT)) {
            effort = new BigDecimal((String) value).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            if (!TasksProperties.isEqual(getProperty(property), value))
                try {
                    asset.setAttributeValue(getDefinition(property.propertyName), value);
                } catch (APIException e) {
                    e.printStackTrace(); //do nothing
                } catch (MetaException e) {
                    e.printStackTrace(); //do nothing
                }
        }
    }

    private IAttributeDefinition getDefinition(String property) {
        IAttributeDefinition res = definitions.get(property);
        if (res == null) {
            res = asset.getAssetType().getAttributeDefinition(property);
            definitions.put(property, res);
        }
        return res;
    }

    public Object getProperty(TasksProperties property) {
        if (property.equals(TasksProperties.EFFORT)) {
            return effort;
        }
        return getValue(property.propertyName);
    }

    public boolean isPropertyChanged(TasksProperties property) {
        boolean value = false;
        if (property.equals(TasksProperties.EFFORT)) {
            value = effort.compareTo(BigDecimal.ZERO) != 0;
        } else try {
            value = asset.getAttributes().get(TASK_PREFIX + property.propertyName).hasChanged();
        } catch (Exception e) {
            //do nothing
        }
        return value;
    }

    public boolean isChanged() {
        return effort.compareTo(BigDecimal.ZERO) != 0 || asset.hasChanged();
    }
}
