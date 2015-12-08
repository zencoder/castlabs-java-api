/**
 * Copyright 2015 Brightcove Inc. All rights reserved.
 * 
 * @author Scott Kidder
 */
package com.brightcove.castlabs.client.response;

/**
 * @author Scott Kidder
 *
 */
public class IngestCENCProtectionDetails {

    private String name;
    private String psshBoxContent;
    private String xmlFragment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPsshBoxContent() {
        return psshBoxContent;
    }

    public void setPsshBoxContent(String psshBoxContent) {
        this.psshBoxContent = psshBoxContent;
    }

    public String getXmlFragment() {
        return xmlFragment;
    }

    public void setXmlFragment(String xmlFragment) {
        this.xmlFragment = xmlFragment;
    }

    @Override
    public String toString() {
        return "IngestCENCProtectionDetails [name=" + name + ", psshBoxContent=" + psshBoxContent
                + ", xmlFragment=" + xmlFragment + "]";
    }

}
