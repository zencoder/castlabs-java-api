/**
 * Copyright 2015 Brightcove Inc. All rights reserved.
 * 
 * @author Scott Kidder
 */
package com.brightcove.castlabs.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Scott Kidder
 *
 */
public class CastlabsClient {

    private static final String CASTLABS_BASE_URL = "https://auth.drmtoday.com/";
    private String baseUrl;
    private String username;
    private String password;
    private int connectionTimeoutSeconds = -1;

    public CastlabsClient(String username, String password) {
        this(username, password, CASTLABS_BASE_URL, -1);
    }

    public CastlabsClient(String username, String password, String baseUrl) {
        this(username, password, baseUrl, -1);
    }

    public CastlabsClient(String username, String password, String baseUrl,
            int connectionTimeoutSeconds) {
        this.username = username;
        this.password = password;
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
        if (baseUrl.endsWith("/")) {
            this.baseUrl = baseUrl;

        } else {
            this.baseUrl = baseUrl + "/";
        }
    }

    /**
     * Login to the Castlabs API endpoint.
     * 
     * @return
     * @throws CastlabsException
     */
    protected String login() throws CastlabsException, IOException {
        final HttpPost loginRequest = new HttpPost(this.baseUrl + "cas/v1/tickets");
        loginRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        loginRequest.setHeader("Accept", "*/*");

        final List<NameValuePair> entityParts = new ArrayList<NameValuePair>();
        entityParts.add(new BasicNameValuePair("username", this.username));
        entityParts.add(new BasicNameValuePair("password", this.password));

        final CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse loginResponse = null;
        try {
            if (this.connectionTimeoutSeconds > 0) {
                int connectionTimeout = connectionTimeoutSeconds * 1000;
                RequestConfig requestConfig =
                        RequestConfig.custom().setConnectionRequestTimeout(connectionTimeout)
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(connectionTimeout).build();
                loginRequest.setConfig(requestConfig);
            }
            loginRequest.setEntity(new UrlEncodedFormEntity(entityParts));
            loginResponse = httpclient.execute(loginRequest);
            if (loginResponse != null) {
                final int statusCode = loginResponse.getStatusLine().getStatusCode();
                final String reason = loginResponse.getStatusLine().getReasonPhrase();
                if (201 != statusCode) {
                    throw new CastlabsException(
                            "Login failed: Response code=" + statusCode + ", Reason=" + reason);
                }
            } else {
                throw new CastlabsException("No response when attempting login to Castlabs");
            }
            final Header locationResponseHeader = loginResponse.getFirstHeader("location");
            if (locationResponseHeader != null
                    && locationResponseHeader.getValue().trim().length() > 0) {
                return locationResponseHeader.getValue();
            } else {
                throw new CastlabsException("No location header provided in API response");
            }
        } finally {
            try {
                if (loginResponse != null)
                    loginResponse.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    
    public String getTicket(String merchantId) throws CastlabsException, IOException {
        final HttpPost ticketRequest = new HttpPost(this.login());
        ticketRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        ticketRequest.setHeader("Accept", "*/*");

        final List<NameValuePair> entityParts = new ArrayList<NameValuePair>();
        entityParts.add(new BasicNameValuePair("service", "https://fe.drmtoday.com/frontend/rest/keys/v1/cenc/merchant/" + merchantId + "/key"));

        final CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse ticketResponse = null;
        try {
            if (this.connectionTimeoutSeconds > 0) {
                int connectionTimeout = connectionTimeoutSeconds * 1000;
                RequestConfig requestConfig =
                        RequestConfig.custom().setConnectionRequestTimeout(connectionTimeout)
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(connectionTimeout).build();
                ticketRequest.setConfig(requestConfig);
            }
            ticketRequest.setEntity(new UrlEncodedFormEntity(entityParts));
            ticketResponse = httpclient.execute(ticketRequest);
            if (ticketResponse != null) {
                final int statusCode = ticketResponse.getStatusLine().getStatusCode();
                final String reason = ticketResponse.getStatusLine().getReasonPhrase();
                if (200 != statusCode) {
                    throw new CastlabsException(
                            "Ticket retrieval failed: Response code=" + statusCode + ", Reason=" + reason);
                }
            } else {
                throw new CastlabsException("No response when retrieving Castlabs ticket");
            }
            return IOUtils.toString(ticketResponse.getEntity().getContent());
        } finally {
            try {
                if (ticketResponse != null)
                    ticketResponse.close();
            } catch (IOException e) {
                // ignore
            }
        }        
    }
}
