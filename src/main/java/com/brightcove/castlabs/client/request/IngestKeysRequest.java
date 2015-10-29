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
    private List<Asset> assets = new ArrayList<Asset>();

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @Override
    public String toString() {
        return "IngestKeysRequest [assets=" + assets + "]";
    }
}
