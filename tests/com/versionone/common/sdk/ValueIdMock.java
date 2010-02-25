package com.versionone.common.sdk;

public class ValueIdMock extends ValueId {

    public ValueIdMock(String name) {
        super(null, name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ValueIdMock))
            return false;
        ValueIdMock other = (ValueIdMock) obj;
        return name.equals(other.name);
    }

}
