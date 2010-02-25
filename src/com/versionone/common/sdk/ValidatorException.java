package com.versionone.common.sdk;

import java.util.List;
import java.util.Map;

import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;

public class ValidatorException extends Exception {

    private static final long serialVersionUID = 1L;

    final Map<Asset, List<RequiredFieldsDTO>> requied;

    ValidatorException(Map<Asset, List<RequiredFieldsDTO>> requiredData, ApiDataLayer dataLayer) {
        super(createErrorMessage(requiredData, dataLayer));
        this.requied = requiredData;
    }

    private static String createErrorMessage(Map<Asset, List<RequiredFieldsDTO>> requiredData, ApiDataLayer dataLayer) {
        final StringBuilder message = new StringBuilder(256);
        for (Asset asset : requiredData.keySet()) {
            final String assetDisplayName = dataLayer.localizerResolve(asset.getAssetType().getDisplayName());
            final String type = asset.getAssetType().getToken();
            final Object id;
            try {
                id = asset.getAttributes().get(type + "." + Entity.ID_PROPERTY).getValue();
            } catch (APIException e) {
                throw new IllegalStateException("Asset " + asset + " haven't attribute: " + type + "."
                        + Entity.ID_PROPERTY, e);
            }
            final String idString = id != null ? id.toString() : "New Items";

            message.append("The following fields are not filled for the ");
            message.append(idString).append(" ").append(assetDisplayName).append(":");
            message.append(getMessageOfUnfilledFieldsList(requiredData.get(asset), "\n\t", "\n\t"));
            message.append("\n");
        }
        return message.toString();
    }

    private static String getMessageOfUnfilledFieldsList(List<RequiredFieldsDTO> unfilledFields, String startWith,
            String delimiter) {
        StringBuilder message = new StringBuilder(startWith);
        ApiDataLayer dataLayer = ApiDataLayer.getInstance();

        for (RequiredFieldsDTO field : unfilledFields) {
            String fieldDisplayName = dataLayer.localizerResolve(field.displayName);
            message.append(fieldDisplayName).append(delimiter);
        }
        message.delete(message.length() - delimiter.length(), message.length());
        return message.toString();
    }
}
