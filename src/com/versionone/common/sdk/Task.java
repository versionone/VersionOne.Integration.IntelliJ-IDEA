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
    private BigDecimal effort = BigDecimal.ZERO;
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
            effort = (BigDecimal) value;
        } else {
            try {
                asset.setAttributeValue(getDefinition(property.propertyName), value);
            } catch (APIException e) {
                System.out.println("Task.setProperty() " + property + " value:" + value);
                System.out.println("\troperty class: " + property.getClass() + " value class:" + value.getClass());
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (MetaException e) {
                System.out.println("Task.setProperty() " + property + " value:" + value);
                System.out.println("\troperty class: " + property.getClass() + " value class:" + value.getClass());
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

//    public boolean isPropertyChanged(TasksProperties property) {
//        return true;
//    }

    public boolean isChanged() {
        return !effort.equals(BigDecimal.ZERO) || asset.hasChanged();
    }
}
