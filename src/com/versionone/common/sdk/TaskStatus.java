package com.versionone.common.sdk;

import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IServices;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.QueryResult;
import com.versionone.apiclient.V1Exception;

/**
 * Implementation of IStatusCodes using VercionOne
 *
 * @author jerry
 */
public final class TaskStatus implements ITaskStatus {
    private static final String TASK_STATUS = "TaskStatus";
    private static final String NAME = "Name";

    private final StatusCode[] statusList;

    /**
     * Create
     *
     * @param metaModel - metamodel to use for obtaining data
     * @param services  - services to use for obtaining data
     * @throws V1Exception - if we cannot read data
     */
    public TaskStatus(IMetaModel metaModel, IServices services) throws V1Exception {
        final IAssetType statusType = metaModel.getAssetType(TASK_STATUS);
        final IAttributeDefinition name = statusType.getAttributeDefinition(NAME);
//        final IAttributeDefinition active = statusType.getAttributeDefinition("Active");

        final Query query = new Query(statusType);
        query.getSelection().add(name);
//        query.setFilter(new FilterTerm(active));
        final QueryResult queryResults = services.retrieve(query);
        final Asset[] assets = queryResults.getAssets();

        statusList = new StatusCode[assets.length + 1];
        statusList[0] = new StatusCode(Oid.Null, "");
        for (int i = 1; i < statusList.length; i++) {
            statusList[i] = new StatusCode(assets[i-1].getOid(), assets[i-1].getAttribute(name).getValue().toString());
        }
    }

    public String getDisplayValue(int index) {
        return statusList[index].name;
    }

    public String[] getDisplayValues() {
        String[] rc = new String[statusList.length];
        for (int i = 0; i < rc.length; i++) {
            rc[i] = statusList[i].name;
        }
        return rc;
    }

    public int getOidIndex(String oid) {
        for (int i = 0; i < statusList.length; i++) {
            if (oid.equals(statusList[i].id))
                return i;
        }
        return 0;
    }

    public String getID(int index) {
        return statusList[index].id;
    }

    public String getDisplayFromOid(String oid) {
        String rc = "*** Invalid OID " + oid + "***";
        for (StatusCode aStatusList : statusList) {
            if (oid.equals(aStatusList.id)) {
                rc = aStatusList.name;
                break;
            }
        }
        return rc;
    }

    /**
     * Represents a VersionOne Status Code
     *
     * @author jerry
     */
    class StatusCode {
        public final String id;
        public final String name;

        StatusCode(Oid oid, String name) {
            id = oid.getToken();
            this.name = name;
        }
    }
}
