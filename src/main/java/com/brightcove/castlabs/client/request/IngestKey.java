/**
 * Copyright 2015 Brightcove Inc. All rights reserved.
 * 
 * @author Scott Kidder
 */
package com.brightcove.castlabs.client.request;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Scott Kidder
 *
 */
public class IngestKey {
    private String keyId;
    private String keyRotationId;
    private StreamType streamType;
    private AlgorithmType algorithm;
    private String key;
    private String iv;
    private String wvAssetId;
    private Map<String, String> keyMetadata = new HashMap<String, String>();

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

    public StreamType getStreamType() {
        return streamType;
    }

    public void setStreamType(StreamType streamType) {
        this.streamType = streamType;
    }

    public AlgorithmType getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AlgorithmType algorithm) {
        this.algorithm = algorithm;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getWvAssetId() {
        return wvAssetId;
    }

    public void setWvAssetId(String wvAssetId) {
        this.wvAssetId = wvAssetId;
    }

    public Map<String, String> getKeyMetadata() {
        return keyMetadata;
    }

    public void setKeyMetadata(Map<String, String> keyMetadata) {
        this.keyMetadata = keyMetadata;
    }

    @Override
    public String toString() {
        return "IngestKey [keyId=" + keyId + ", keyRotationId=" + keyRotationId + ", streamType="
                + streamType + ", algorithm=" + algorithm + ", key=" + key + ", iv=" + iv
                + ", wvAssetId=" + wvAssetId + ", keyMetadata=" + keyMetadata + "]";
    }
}
