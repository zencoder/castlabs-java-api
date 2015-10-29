/**
 * Copyright 2015 Brightcove Inc. All rights reserved.
 * 
 * @author Scott Kidder
 */
package com.brightcove.castlabs.client.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Scott Kidder
 *
 */
public class Asset {
    private String assetId;
    private List<IngestKeysResponse> keys = new ArrayList<IngestKeysResponse>();
    private List<IngestAssetError> errors = new ArrayList<IngestAssetError>();

    public List<IngestAssetError> getErrors() {
        return errors;
    }

    public void setErrors(List<IngestAssetError> errors) {
        this.errors = errors;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public List<IngestKeysResponse> getKeys() {
        return keys;
    }

    public void setKeys(List<IngestKeysResponse> keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "Asset [assetId=" + assetId + ", keys=" + keys + ", errors=" + errors + "]";
    }
}
