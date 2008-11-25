/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.util.ui.Table;
import com.versionone.common.sdk.IDataLayer;

public class DetailsTable extends Table {

    private final IDataLayer data;

    public DetailsTable(VerticalTableModel v1TableModel, IDataLayer data) {
        super(v1TableModel);
        this.data = data;
    }
}
