package com.brightcove.castlabs.client.response;

public class DownloadKeysResponse {

    public String uuid;
    public String type;
    public String keyId;
    public String streamType;
    public String assetId;
    public String variantId;
    public String keyRotationId;
    public String ingestChannel;
    public String ingestRegion;
    public String auditChanged;
    public String auditChangedBy;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
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

    public String getKeyRotationId() {
        return keyRotationId;
    }

    public void setKeyRotationId(String keyRotationId) {
        this.keyRotationId = keyRotationId;
    }

    public String getIngestChannel() {
        return ingestChannel;
    }

    public void setIngestChannel(String ingestChannel) {
        this.ingestChannel = ingestChannel;
    }

    public String getIngestRegion() {
        return ingestRegion;
    }

    public void setIngestRegion(String ingestRegion) {
        this.ingestRegion = ingestRegion;
    }

    public String getAuditChanged() {
        return auditChanged;
    }

    public void setAuditChanged(String auditChanged) {
        this.auditChanged = auditChanged;
    }

    public String getAuditChangedBy() {
        return auditChangedBy;
    }

    public void setAuditChangedBy(String auditChangedBy) {
        this.auditChangedBy = auditChangedBy;
    }

    @Override
    public String toString() {
        return "DownloadKeysResponse{" +
                "uuid='" + uuid + '\'' +
                ", type='" + type + '\'' +
                ", keyId='" + keyId + '\'' +
                ", streamType='" + streamType + '\'' +
                ", assetId='" + assetId + '\'' +
                ", variantId='" + variantId + '\'' +
                ", keyRotationId='" + keyRotationId + '\'' +
                ", ingestChannel='" + ingestChannel + '\'' +
                ", ingestRegion='" + ingestRegion + '\'' +
                ", auditChanged='" + auditChanged + '\'' +
                ", auditChangedBy='" + auditChangedBy + '\'' +
                '}';
    }

}
