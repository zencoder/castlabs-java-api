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
public class IngestAssetsResponse {
    private List<AssetResponse> assets = new ArrayList<AssetResponse>();

    public List<AssetResponse> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetResponse> assets) {
        this.assets = assets;
    }

    @Override
    public String toString() {
        return "IngestAssetsResponse [assets=" + assets + "]";
    }

}
