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
public class IngestKeysResponse {

    private String keyId;
    private String keyRotationId;
    private IngestCENCResponse cencResponse;
    private List<IngestAssetKeyError> errors = new ArrayList<IngestAssetKeyError>();

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyRotationId() {
        return keyRotationId;
    }

    public void setKeyRotationId(String keyRotationId) {
        this.keyRotationId = keyRotationId;
    }

    public IngestCENCResponse getCencResponse() {
        return cencResponse;
    }

    public void setCencResponse(IngestCENCResponse cencResponse) {
        this.cencResponse = cencResponse;
    }

    public List<IngestAssetKeyError> getErrors() {
        return errors;
    }

    public void setErrors(List<IngestAssetKeyError> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "IngestKeysResponse [keyId=" + keyId + ", keyRotationId=" + keyRotationId
                + ", cencResponse=" + cencResponse + ", errors=" + errors + "]";
    }
}
