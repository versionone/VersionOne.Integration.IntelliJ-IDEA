package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.versionone.apiclient.*;

public class PrimaryWorkitem extends Workitem {

    public static final String PARENT_PROPERTY = "Parent";

    /**
     * List of child SecondaryWorkitems.
     */
    public final List<SecondaryWorkitem> children;

    protected PrimaryWorkitem(ApiDataLayer dataLayer, Asset asset) {
        super(dataLayer, asset);

        children = new ArrayList<SecondaryWorkitem>(asset.getChildren().size());
        for (Asset childAsset : asset.getChildren()) {
            if (dataLayer.isDisplayed(childAsset)) {
                children.add(new SecondaryWorkitem(dataLayer, childAsset, this));
            }
        }
        Comparator<Entity> comparator = new Comparator<Entity>() {
            public int compare(Entity entity1, Entity entity2) {
            	if (entity1.getType().equals(EntityType.Test) && entity2.getType().equals(EntityType.Task)) {
            		return -1;
            	}
            	if (entity1.getType().equals(EntityType.Task) && entity2.getType().equals(EntityType.Test)) {
            		return 1;
            	}
            	String value1 = entity1.getProperty(Workitem.ORDER_PROPERTY).toString();
            	String value2 = entity2.getProperty(Workitem.ORDER_PROPERTY).toString();
            	return Integer.valueOf(value1).compareTo(Integer.valueOf(value2));
            }
        };
        Collections.sort(children, comparator);
    }

    @Override
    public void commitChanges() throws DataLayerException, ValidatorException {
        final boolean persistent = isPersistent();
        super.commitChanges();
        if (!persistent && !children.isEmpty()) {
            for (SecondaryWorkitem child : children) {
                try {
                    ApiDataLayer.setAssetAttribute(child.asset, PARENT_PROPERTY, asset.getOid());
                } catch (MetaException e) {
                    throw new DataLayerException("Set attribute Parent to asset: " + child.asset, e);
                } catch (APIException e) {
                    throw new DataLayerException("Set attribute Parent to asset: " + child.asset, e);
                }
            }
        }
    }

    public SecondaryWorkitem createChild(EntityType type) throws DataLayerException {
        return dataLayer.createNewSecondaryWorkitem(type, this);
    }
}
