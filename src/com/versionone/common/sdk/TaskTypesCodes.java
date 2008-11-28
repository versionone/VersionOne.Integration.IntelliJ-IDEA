/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.common.sdk;

import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.FilterTerm;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IServices;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.QueryResult;
import com.versionone.apiclient.V1Exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class TaskTypesCodes {
    private final Map<Oid, String> map;

    public TaskTypesCodes(IMetaModel metaModel, IServices services) throws V1Exception {
        final IAssetType taskTypes = metaModel.getAssetType("TaskCategory");
        final IAttributeDefinition name = taskTypes.getAttributeDefinition("Name");
        final IAttributeDefinition inactive = taskTypes.getAttributeDefinition("Inactive");

        final Query query = new Query(taskTypes);
        query.getSelection().add(name);
        query.setFilter(new FilterTerm(inactive, FilterTerm.Operator.Equal, "False"));
        final QueryResult queryResults = services.retrieve(query);
        final Asset[] assets = queryResults.getAssets();

        map = new HashMap<Oid, String>(assets.length);
        for (Asset asset : assets) {
            map.put(asset.getOid(), (String) asset.getAttribute(name).getValue());
        }
    }

    public Vector<String> getDisplayValues() {
        //Names
        final Vector<String> vector = new Vector<String>(map.size());
        vector.add(null);
        for (String s : map.values()) {
            vector.add(s);
        }
        return vector;
    }

    /**
     * More slow operation then others.
     */
    public Oid getID(String value) {
        //Name -> Oid ("Admin" -> "TaskCategory:123")
        if (value == null)
            return Oid.Null;
        for (Oid oid : map.keySet()) {
            final String name = map.get(oid);
            if (name.equals(value)) {
                return oid;
            }
        }
        return null;
    }

    public String getDisplayFromOid(Oid oid) {
        //oid -> Name
        if (oid.isNull())
            return null;
        return map.get(oid);
    }
}
