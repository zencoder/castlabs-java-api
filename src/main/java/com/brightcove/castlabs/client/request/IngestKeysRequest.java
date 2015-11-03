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
public class IngestKeysRequest {
    private List<AssetRequest> assets = new ArrayList<AssetRequest>();

    public List<AssetRequest> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetRequest> assets) {
        this.assets = assets;
    }

    @Override
    public String toString() {
        return "IngestKeysRequest [assets=" + assets + "]";
    }
}
