package com.versionone.common.sdk;

public class AttributeInfo {
    public final String attr;
    public final EntityType type;
    public final boolean isList;

    public AttributeInfo(String attr, EntityType type, boolean isList) {
        if (attr == null || type == null) {
            throw new IllegalArgumentException("Parameters cannot be null.");
        }
        this.attr = attr;
        this.type = type;
        this.isList = isList;
    }

    @Override
    public String toString() {
        return type + "." + attr + "(List:" + Boolean.toString(isList) + ")";
    }

    @Override
    public int hashCode() {
        int result = attr.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (isList ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof AttributeInfo))
            return false;
        AttributeInfo other = (AttributeInfo) obj;
        if (!attr.equals(other.attr))
            return false;
        if (type != other.type)
            return false;
        if (isList != other.isList)
            return false;
        return true;
    }
}
