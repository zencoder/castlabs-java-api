/**
 * Copyright 2015 Brightcove Inc. All rights reserved.
 * 
 * @author Scott Kidder
 */
package com.brightcove.castlabs.client.request;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Scott Kidder
 *
 */
public class AssetRequest {
    private AssetType type;
    private String assetId;
    private String variantId;
    private List<IngestKey> ingestKeys = new ArrayList<IngestKey>();

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public List<IngestKey> getIngestKeys() {
        return ingestKeys;
    }

    public void setIngestKeys(List<IngestKey> ingestKeys) {
        this.ingestKeys = ingestKeys;
    }

    @Override
    public String toString() {
        return "Asset [type=" + type + ", assetId=" + assetId + ", variantId=" + variantId
                + ", ingestKeys=" + ingestKeys + "]";
    }
}
