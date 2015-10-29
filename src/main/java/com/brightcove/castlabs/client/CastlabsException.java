/**
 * Copyright 2015 Brightcove Inc. All rights reserved.
 * @author Scott Kidder
 */
package com.brightcove.castlabs.client;

/**
 * @author Scott Kidder
 *
 */
public class CastlabsException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 9080324184558489684L;

    /**
     * @param msg
     */
    public CastlabsException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param e
     */
    public CastlabsException(String msg, Throwable e) {
        super(msg, e);
    }

}
