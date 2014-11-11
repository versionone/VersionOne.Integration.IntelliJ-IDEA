package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.versionone.Oid;
import com.versionone.apiclient.AndFilterTerm;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.FilterTerm;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IFilterTerm;
import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IServices;
import com.versionone.apiclient.MetaException;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.V1Exception;

class RequiredFieldsValidator {

    private final IMetaModel metaModel;
    private final IServices services;
    private final Map<EntityType, List<RequiredFieldsDTO>> requiredList;

    RequiredFieldsValidator(IMetaModel metaModel, IServices services) {
        this.metaModel = metaModel;
        this.services = services;
        requiredList = new HashMap<EntityType, List<RequiredFieldsDTO>>(EntityType.values().length);
    }

    private List<RequiredFieldsDTO> queryRequiredFields(String assetType) throws DataLayerException {
        final IAssetType attributeDefinitionType = metaModel.getAssetType("AttributeDefinition");
        final IAttributeDefinition nameDef = attributeDefinitionType.getAttributeDefinition("Name");
        final IAttributeDefinition assetNameDef = attributeDefinitionType
                .getAttributeDefinition("Asset.AssetTypesMeAndDown.Name");
        final IAssetType taskType = metaModel.getAssetType(assetType);

        final Query query = new Query(attributeDefinitionType);
        query.getSelection().add(nameDef);
        final FilterTerm assetTypeTerm = new FilterTerm(assetNameDef);
        assetTypeTerm.Equal(assetType);
        query.setFilter(new AndFilterTerm(new IFilterTerm[] { assetTypeTerm }));

        final List<RequiredFieldsDTO> fields = new LinkedList<RequiredFieldsDTO>();
        try {
            for (Asset asset : services.retrieve(query).getAssets()) {
                final String name = asset.getAttribute(nameDef).getValue().toString();
                if (isRequiredField(taskType, name)) {
                    RequiredFieldsDTO reqFieldData = new RequiredFieldsDTO(name, taskType.getAttributeDefinition(name)
                            .getDisplayName());
                    fields.add(reqFieldData);
                }
            }
        } catch (MetaException e) {
            throw ApiDataLayer.createAndLogException("Cannot get meta data for " + assetType, e);
        } catch (V1Exception e) {
            throw ApiDataLayer.createAndLogException("Cannot get meta data for " + assetType, e);
        }

        return fields;
    }

    private boolean isRequiredField(IAssetType taskType, String name) {
        IAttributeDefinition def = taskType.getAttributeDefinition(name);
        return def.isRequired() && !def.isReadOnly();
    }

    Map<Asset, List<RequiredFieldsDTO>> validate(List<Asset> assets) {
        Map<Asset, List<RequiredFieldsDTO>> requiredData = new HashMap<Asset, List<RequiredFieldsDTO>>();
        for (Asset asset : assets) {
            List<RequiredFieldsDTO> fields = validate(asset);

            if (fields.size() > 0) {
                requiredData.put(asset, fields);
            }
            requiredData.putAll(validate(asset.getChildren()));
        }
        return requiredData;
    }

    List<RequiredFieldsDTO> validate(Asset asset) {
        final EntityType type = EntityType.valueOf(asset.getAssetType().getToken());

        if (!requiredList.containsKey(type)) {
            return Collections.emptyList();
        }

        final List<RequiredFieldsDTO> unfilledFields = new ArrayList<RequiredFieldsDTO>();
        for (RequiredFieldsDTO field : requiredList.get(type)) {
            final String fullName = type + "." + field.name;
            final Attribute attribute = asset.getAttributes().get(fullName);
            assert (attribute != null);
            if (!isFilled(attribute)) {
                unfilledFields.add(field);
            }
        }
        return unfilledFields;
    }

    private static boolean isFilled(Attribute attribute) {
        final Object[] values = attribute.getValues();
        if (values == null || values.length == 0){
            return false;
        } else if (values[0] instanceof Oid){
            return !((Oid)values[0]).isNull();
        } else {
            return true;
        }
    }

    public Map<EntityType, List<RequiredFieldsDTO>> init() throws DataLayerException {
        EntityType[] types = EntityType.values();
        for (EntityType type : types) {
            if (type.isWorkitem()) {
                requiredList.put(type, queryRequiredFields(type.name()));
            }
        }
        return requiredList;
    }

    public List<RequiredFieldsDTO> getFields(EntityType type) {
        return requiredList.get(type);
    }
}
