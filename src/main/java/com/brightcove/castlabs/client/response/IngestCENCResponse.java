/**
 * Copyright 2015 Brightcove Inc. All rights reserved.
 * 
 * @author Scott Kidder
 */
package com.brightcove.castlabs.client.response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Scott Kidder
 *
 */
public class IngestCENCResponse {

    Map<String, IngestCENCProtectionDetails> systemId =
            new HashMap<String, IngestCENCProtectionDetails>();

    public Map<String, IngestCENCProtectionDetails> getSystemId() {
        return systemId;
    }

    public void setSystemId(Map<String, IngestCENCProtectionDetails> systemId) {
        this.systemId = systemId;
    }

    @Override
    public String toString() {
        return "IngestCENCResponse [systemId=" + systemId + "]";
    }

}
