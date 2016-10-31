/**
 * Copyright 2015 Brightcove Inc. All rights reserved.
 */
package com.brightcove.castlabs.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
import com.brightcove.castlabs.client.response.IngestAssetsResponse;
import com.brightcove.castlabs.client.request.AddSubMerchantAccountRequest;
import com.brightcove.castlabs.client.response.AddSubMerchantAccountResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Client for interacting with the Castlabs API.
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

    public CastlabsClient(final String username, final String password) {
        this(username, password, CASTLABS_AUTH_BASE_URL, CASTLABS_INGESTION_BASE_URL, -1);
    }

    public CastlabsClient(final String username, final String password, final String authBaseUrl, final String ingestionBaseUrl, final int connectionTimeoutSeconds) {
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
     * @throws IOException       communication error when interacting with Castlabs API
     */
    protected String login() throws CastlabsException, IOException {
        final HttpPost loginRequest = new HttpPost(this.authBaseUrl + "cas/v1/tickets");
        loginRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        loginRequest.setHeader("Accept", "*/*");

        final List<NameValuePair> entityParts = new ArrayList<NameValuePair>();
        entityParts.add(new BasicNameValuePair("username", this.username));
        entityParts.add(new BasicNameValuePair("password", this.password));

        if (this.connectionTimeoutSeconds > 0) {
            int connectionTimeout = connectionTimeoutSeconds * 1000;
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectionRequestTimeout(connectionTimeout)
                            .setConnectTimeout(connectionTimeout)
                            .setSocketTimeout(connectionTimeout).build();
            loginRequest.setConfig(requestConfig);
        }
        loginRequest.setEntity(new UrlEncodedFormEntity(entityParts));

        final CloseableHttpClient httpclient = HttpClients.createDefault();
        try (CloseableHttpResponse loginResponse = httpclient.execute(loginRequest)) {
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
        }
    }


    /**
     * Retrieve an authentication ticket with the given URL and return the URL with token appended
     *
     * @param url URL to request the token for
     * @return ticket that can be used to ingest encryption keys
     * @throws CastlabsException error reported by Castlabs
     * @throws IOException       communication error when interacting with Castlabs API
     */
    protected String getUrlWithTicket(final String url) throws CastlabsException, IOException {
        final HttpPost ticketRequest = new HttpPost(this.login());
        ticketRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        ticketRequest.setHeader("Accept", "*/*");

        final List<NameValuePair> entityParts = new ArrayList<>();
        entityParts.add(new BasicNameValuePair("service", url));

        if (this.connectionTimeoutSeconds > 0) {
            int connectionTimeout = connectionTimeoutSeconds * 1000;
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectionRequestTimeout(connectionTimeout)
                            .setConnectTimeout(connectionTimeout)
                            .setSocketTimeout(connectionTimeout).build();
            ticketRequest.setConfig(requestConfig);
        }
        ticketRequest.setEntity(new UrlEncodedFormEntity(entityParts));

        final CloseableHttpClient httpclient = HttpClients.createDefault();
        try (CloseableHttpResponse ticketResponse = httpclient.execute(ticketRequest)) {
            if (ticketResponse != null) {
                final int statusCode = ticketResponse.getStatusLine().getStatusCode();
                if (200 != statusCode) {
                    final String reason = ticketResponse.getStatusLine().getReasonPhrase();
                    final String responseBody =
                            IOUtils.toString(ticketResponse.getEntity().getContent());
                    throw new CastlabsException("Ticket retrieval failed: Response code="
                            + statusCode + ", Reason=" + reason + ", Body=" + responseBody);
                }
            } else {
                throw new CastlabsException("No response when retrieving Castlabs ticket");
            }
            return url + "?ticket=" + IOUtils.toString(ticketResponse.getEntity().getContent());
        }
    }

    /**
     * Ingest one or more keys into the Castlabs keystore.
     *
     * @param request Request parameters to pass to Castlabs
     * @param merchantId
     * @return response from Castlabs
     * @throws CastlabsException error reported by Castlabs
     * @throws IOException       network error while communicating with Castlabs REST API
     */
    public IngestAssetsResponse ingestKeys(final IngestKeysRequest request, final String merchantId)
            throws CastlabsException, IOException {

        final String uri = this.getUrlWithTicket(this.ingestionBaseUrl + "frontend/api/keys/v2/ingest/" + merchantId);
        final HttpPost ingestRequest = new HttpPost(uri);
        ingestRequest.addHeader("Content-Type", "application/json");
        ingestRequest.setHeader("Accept", "application/json");

        if (this.connectionTimeoutSeconds > 0) {
            int connectionTimeout = connectionTimeoutSeconds * 1000;
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectionRequestTimeout(connectionTimeout)
                            .setConnectTimeout(connectionTimeout)
                            .setSocketTimeout(connectionTimeout).build();
            ingestRequest.setConfig(requestConfig);
        }
        ingestRequest.setEntity(new StringEntity(objectMapper.writeValueAsString(request)));

        final CloseableHttpClient httpclient = HttpClients.createDefault();
        try (CloseableHttpResponse ingestResponse = httpclient.execute(ingestRequest)) {
            if (ingestResponse != null) {
                final int statusCode = ingestResponse.getStatusLine().getStatusCode();
                if (200 != statusCode) {
                    final String reason = ingestResponse.getStatusLine().getReasonPhrase();
                    final String responseBody =
                            IOUtils.toString(ingestResponse.getEntity().getContent());
                    throw new CastlabsException("Ingest failed: Response code=" + statusCode
                            + ", Reason=" + reason + ", Body=" + responseBody);
                }
                final HttpEntity responseEntity = ingestResponse.getEntity();
                if (responseEntity != null) {
                    return objectMapper.readValue(responseEntity.getContent(), IngestAssetsResponse.class);
                } else {
                    throw new CastlabsException("Empty response entity from Castlabs");
                }
            } else {
                throw new CastlabsException("No response when ingesting keys into Castlabs");
            }
        }
    }

    /**
     * Add a sub merchant account to Castlabs.
     *
     * @param request Request parameters to pass to Castlabs
     * @param merchantUuid UUID for the merchant that the sub-merchant is being created off
     * @return response from Castlabs
     * @throws CastlabsException error reported by Castlabs
     * @throws IOException       network error while communicating with Castlabs REST API
     */
    public AddSubMerchantAccountResponse addSubMerchantAccount(final AddSubMerchantAccountRequest request, final String merchantUuid)
            throws IOException, CastlabsException {

        final String uri = this.getUrlWithTicket(this.ingestionBaseUrl + "frontend/rest/reselling/v1/reseller/" + merchantUuid + "/submerchant/add");
        final HttpPost addResellerRequest = new HttpPost(uri);
        addResellerRequest.addHeader("Content-Type", "application/json");
        addResellerRequest.setHeader("Accept", "application/json");

        if (this.connectionTimeoutSeconds > 0) {
            int connectionTimeout = connectionTimeoutSeconds * 1000;
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectionRequestTimeout(connectionTimeout)
                            .setConnectTimeout(connectionTimeout)
                            .setSocketTimeout(connectionTimeout).build();
            addResellerRequest.setConfig(requestConfig);
        }
        addResellerRequest.setEntity(new StringEntity(objectMapper.writeValueAsString(request)));

        final CloseableHttpClient httpclient = HttpClients.createDefault();
        try (CloseableHttpResponse httpResponse = httpclient.execute(addResellerRequest)){
            final HttpEntity responseEntity = httpResponse.getEntity();
            if (responseEntity == null) {
                throw new CastlabsException("Empty response entity from Castlabs. HTTP Status: " + httpResponse.getStatusLine().getStatusCode());
            }

            final String responseBody = IOUtils.toString(responseEntity.getContent());
            if(StringUtils.isBlank(responseBody)) {
                throw new CastlabsException("Empty response entity from Castlabs. HTTP Status: " + httpResponse.getStatusLine().getStatusCode());
            }

            final AddSubMerchantAccountResponse response = objectMapper.readValue(responseBody, AddSubMerchantAccountResponse.class);
            if(response.getSubMerchantUuid() == null) {
                throw new CastlabsException("Unexpected response from Castlabs: " + responseBody);
            }
            return response;
        }
    }

}
