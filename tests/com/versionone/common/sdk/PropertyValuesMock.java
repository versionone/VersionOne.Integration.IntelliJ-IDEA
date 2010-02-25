package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.versionone.Oid;

public class PropertyValuesMock extends PropertyValues {

    public ArrayList<ValueId> valueIds;

    PropertyValuesMock() {
        super();
        valueIds = new ArrayList<ValueId>();
    }

    public PropertyValuesMock(String... values) {
        super();
        valueIds  = new ArrayList<ValueId>(values.length);
        for (String value : values) {
            valueIds.add(new ValueIdMock(value));
        }
    }

    @Override
    public boolean contains(ValueId valueId) {
        return valueIds.contains(valueId);
    }

    @Override
    boolean containsOid(Oid value) {
        throw new NotImplementedException();
    }

    @Override
    ValueId find(Oid oid) {
        throw new NotImplementedException();
    }

    @Override
    public int getStringArrayIndex(ValueId value) {
        return valueIds.indexOf(value.toString());
    }

    @Override
    public ValueId getValueIdByIndex(int value) {
        return valueIds.get(value);
    }

    @Override
    public Iterator<ValueId> iterator() {
        return valueIds.iterator();
    }

    @Override
    public int size() {
        return valueIds.size();
    }

    @Override
    PropertyValues subset(Object[] oids) {
        throw new NotImplementedException();
    }

    @Override
    public ValueId[] toArray() {
        ValueId[] res = new ValueId[valueIds.size()];
        valueIds.toArray(res);
        return res;
    }

    @Override
    public String[] toStringArray() {
        String[] res = new String[size()];
        for (ValueId data : valueIds) {
            res[valueIds.indexOf(data)] = data.toString();
        }
        return res;
    }

    @Override
    public String toString() {
        if (valueIds.isEmpty()) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        Iterator<ValueId> i = iterator();
        res.append(i.next());
        while (i.hasNext()) {
            res.append(", ").append(i.next());
        }
        return res.toString();
    }
}
