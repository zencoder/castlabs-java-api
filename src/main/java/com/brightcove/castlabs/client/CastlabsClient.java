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
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.brightcove.castlabs.client.request.IngestKeysRequest;
import com.brightcove.castlabs.client.response.IngestKeysResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Client for interacting with the Castlabs key ingestion API.
 * 
 * @author Scott Kidder
 *
 */
public class CastlabsClient {

    private static final String CASTLABS_AUTH_BASE_URL = "https://auth.drmtoday.com/";
    private static final String CASTLABS_INGESTION_BASE_URL = "https://fe.drmtoday.com/";
    private String authBaseUrl;
    private String ingestionBaseUrl;
    private String username;
    private String password;
    private int connectionTimeoutSeconds = -1;
    private ObjectMapper objectMapper;

    public CastlabsClient(String username, String password) {
        this(username, password, CASTLABS_AUTH_BASE_URL, CASTLABS_INGESTION_BASE_URL, -1);
    }

    public CastlabsClient(String username, String password, String authBaseUrl,
            String ingestionBaseUrl, int connectionTimeoutSeconds) {
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.username = username;
        this.password = password;
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;

        if (authBaseUrl.endsWith("/")) {
            this.authBaseUrl = authBaseUrl;
        } else {
            this.authBaseUrl = authBaseUrl + "/";
        }

        if (ingestionBaseUrl.endsWith("/")) {
            this.ingestionBaseUrl = ingestionBaseUrl;
        } else {
            this.ingestionBaseUrl = ingestionBaseUrl + "/";
        }
    }

    /**
     * Login to the Castlabs API endpoint.
     * 
     * @return a ticket URL
     * @throws CastlabsException error reported by Castlabs
     * @throws IOException communication error when interacting with Castlabs API
     */
    protected String login() throws CastlabsException, IOException {
        final HttpPost loginRequest = new HttpPost(this.authBaseUrl + "cas/v1/tickets");
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


    /**
     * Retrieve an authentication ticket with the given merchant ID.
     * 
     * @param merchantId Castlabs-issued merchant ID
     * @return ticket that can be used to ingest encryption keys
     * @throws CastlabsException error reported by Castlabs
     * @throws IOException communication error when interacting with Castlabs API
     */
    protected String getTicket(String merchantId) throws CastlabsException, IOException {
        final HttpPost ticketRequest = new HttpPost(this.login());
        ticketRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        ticketRequest.setHeader("Accept", "*/*");

        final List<NameValuePair> entityParts = new ArrayList<NameValuePair>();
        entityParts.add(new BasicNameValuePair("service",
                this.ingestionBaseUrl + "frontend/api/keys/v2/ingest/" + merchantId));

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
                    throw new CastlabsException("Ticket retrieval failed: Response code="
                            + statusCode + ", Reason=" + reason);
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

    public IngestKeysResponse ingestKeys(IngestKeysRequest assets, String merchantId)
            throws CastlabsException, IOException {
        final String uri = this.ingestionBaseUrl + "frontend/api/keys/v2/ingest/" + merchantId
                + "?ticket=" + this.getTicket(merchantId);
        final HttpPost ingestRequest = new HttpPost(uri);
        ingestRequest.addHeader("Content-Type", "application/json");
        ingestRequest.setHeader("Accept", "application/json");

        final CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse ticketResponse = null;
        try {
            if (this.connectionTimeoutSeconds > 0) {
                int connectionTimeout = connectionTimeoutSeconds * 1000;
                RequestConfig requestConfig =
                        RequestConfig.custom().setConnectionRequestTimeout(connectionTimeout)
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(connectionTimeout).build();
                ingestRequest.setConfig(requestConfig);
            }
            ingestRequest.setEntity(new StringEntity(objectMapper.writeValueAsString(assets)));
            ticketResponse = httpclient.execute(ingestRequest);
            if (ticketResponse != null) {
                final int statusCode = ticketResponse.getStatusLine().getStatusCode();
                final String reason = ticketResponse.getStatusLine().getReasonPhrase();
                if (200 != statusCode) {
                    throw new CastlabsException(
                            "Ingest failed: Response code=" + statusCode + ", Reason=" + reason);
                }
                final HttpEntity responseEntity = ticketResponse.getEntity();
                if (responseEntity != null) {
                    final IngestKeysResponse getPadResponse = objectMapper
                            .readValue(responseEntity.getContent(), IngestKeysResponse.class);
                    return getPadResponse;
                } else {
                    throw new CastlabsException("Empty response entity from Castlabs");
                }
            } else {
                throw new CastlabsException("No response when ingesting keys into Castlabs");
            }
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
