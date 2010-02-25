package com.versionone.common.sdk;

import java.util.HashMap;
import java.util.Map;

public class PrimaryWorkitemMock extends PrimaryWorkitem {

    public final String id;
    public final EntityType type;
    public final Map<String, Object> properties = new HashMap<String, Object>();
    public boolean hasChanges;

    public PrimaryWorkitemMock() {
        this(null, null, null);
    }

    public PrimaryWorkitemMock(ApiDataLayer dataLayer, String id, EntityType type) {
        super(dataLayer, new AssetMock(null));
        this.type = type;
        this.id = id == null ? "" : type.name();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getProperty(String propertyName) {
        final Object res = properties.get(propertyName);
        return res == null ? "***Not defined***" : res;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public boolean hasChanges() {
        return hasChanges;
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        properties.put(propertyName, newValue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = (hasChanges ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        PrimaryWorkitemMock other = (PrimaryWorkitemMock) obj;
        if (hasChanges != other.hasChanges)
            return false;
        if (!id.equals(other.id))
            return false;
        if (!type.equals(other.type))
            return false;
        return true;
    }
}
