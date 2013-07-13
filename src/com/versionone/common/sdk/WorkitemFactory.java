package com.versionone.common.sdk;

import com.versionone.apiclient.*;

import java.util.Set;

public class WorkitemFactory {

    final ApiDataLayer dataLayer;
    final String currentProjectId;
    final Set<AttributeInfo> attributesToQuery;

    WorkitemFactory(ApiDataLayer dataLayer, String currentProjectId, Set<AttributeInfo> attributesToQuery) {
        this.dataLayer = dataLayer;
        this.currentProjectId = currentProjectId;
        this.attributesToQuery = attributesToQuery;
    }

    /**
     * Creates new Story or Defect.
     *
     * @param entityType of new Workitem.
     * @param assetType of new Workitem.
     * @return newly created Workitem.
     * @throws DataLayerException       if any error.
     * @throws IllegalArgumentException when prefix or parent isn't a Workitem, or trying to create a
     *                                  wrong Workitem hierarchy.
     */
    PrimaryWorkitem createNewPrimaryWorkitem(EntityType entityType, IAssetType assetType) throws DataLayerException {
        try {
            if (!entityType.isPrimary()) {
                throw new IllegalArgumentException("Wrong type:" + entityType);
            }
            final Asset asset = createNewAsset(entityType, assetType);
            final Project project = dataLayer.getCurrentProject();
            loadAssetAttribute(asset, "Scope.Name", project.getProperty(Entity.NAME_PROPERTY));
            loadAssetAttribute(asset, "Timebox.Name", project.getProperty("Schedule.EarliestActiveTimebox.Name"));
            ApiDataLayer.setAssetAttribute(asset, "Scope", currentProjectId);
            ApiDataLayer.setAssetAttribute(asset, "Timebox", project.getProperty("Schedule.EarliestActiveTimebox"));

            return new PrimaryWorkitem(dataLayer, asset);
        } catch (MetaException ex) {
            throw new DataLayerException("Cannot create workitem: " + entityType, ex);
        } catch (APIException ex) {
            throw new DataLayerException("Cannot create workitem: " + entityType, ex);
        }
    }

    private Asset createNewAsset(EntityType entityType, IAssetType assetType) throws APIException {
        final Asset asset = new Asset(assetType);
        for (AttributeInfo attrInfo : attributesToQuery) {
            if (attrInfo.type == entityType) {
                ApiDataLayer.setAssetAttribute(asset, attrInfo.attr, null);
            }
        }
        return asset;
    }

    SecondaryWorkitem createNewSecondaryWorkitem(EntityType entityType, IAssetType assetType, PrimaryWorkitem parent)
            throws DataLayerException {
        try {
            if (!entityType.isSecondary()) {
                throw new IllegalArgumentException("Wrong type:" + entityType);
            }
            final Asset asset = createNewAsset(entityType, assetType);

            loadAssetAttribute(asset, "Scope.Name", dataLayer.getCurrentProject().getProperty(Entity.NAME_PROPERTY));

            if (parent == null || parent.getType().isSecondary()) {
                throw new IllegalArgumentException("Cannot create " + asset.getAssetType() + " as children of "
                        + parent);
            }
            ApiDataLayer.setAssetAttribute(asset, "Parent", parent.asset.getOid());

            loadAssetAttribute(asset, "Parent.Name", parent.getProperty(Entity.NAME_PROPERTY));
            loadAssetAttribute(asset, "Timebox.Name", parent.getProperty("Timebox.Name"));

            final SecondaryWorkitem item = new SecondaryWorkitem(dataLayer, asset, parent);
            parent.children.add(item);
            parent.asset.getChildren().add(item.asset);
            return item;
        } catch (MetaException ex) {
            throw new DataLayerException("Cannot create workitem: " + entityType, ex);
        } catch (APIException ex) {
            throw new DataLayerException("Cannot create workitem: " + entityType, ex);
        }
    }

    private void loadAssetAttribute(Asset asset, String string, Object property) throws APIException {
        final IAttributeDefinition def = asset.getAssetType().getAttributeDefinition(string);
        asset.loadAttributeValue(def, property);
    }
}
