/**
 * Copyright 2015 Brightcove Inc. All rights reserved.
 * 
 * @author Scott Kidder
 */
package com.brightcove.castlabs.client;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;

import com.brightcove.castlabs.client.request.Asset;
import com.brightcove.castlabs.client.request.IngestKey;
import com.brightcove.castlabs.client.request.IngestKeysRequest;
import com.brightcove.castlabs.client.request.StreamType;
import com.brightcove.castlabs.client.response.IngestKeysResponse;

import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Scott Kidder
 *
 */
public class CastlabsClientTest {

    private static final int mockPort = 1080;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(mockPort, this);

    private MockServerClient mockServerClient;

    private CastlabsClient castlabsClient;

    private String username;
    private String password;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        username = "x";
        password = "y";
        String mockURL = "http://localhost:" + mockPort;
        castlabsClient = new CastlabsClient(username, password, mockURL, mockURL, 1);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        castlabsClient = null;
    }

    @Test
    public void testUnauthorizedLoginRequest() {
        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets");
        mockServerClient.when(loginRequest).respond(response().withStatusCode(401));
        try {
            castlabsClient.login();
            fail("Expected exception");
        } catch (CastlabsException e) {
            // ignore
        } catch (Exception e) {
            fail(e.getMessage());
        }
        mockServerClient.verify(loginRequest, VerificationTimes.once());
    }

    @Test
    public void testLoginRequestThatOmitsLocationHeaderInResponse() {
        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets");
        mockServerClient.when(loginRequest).respond(response().withStatusCode(201));
        try {
            castlabsClient.login();
            fail("Expected exception");
        } catch (CastlabsException e) {
            // ignore
        } catch (Exception e) {
            fail(e.getMessage());
        }
        mockServerClient.verify(loginRequest, VerificationTimes.once());
    }

    @Test
    public void testLoginRequestTimeout() {
        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets");
        mockServerClient.when(loginRequest).respond(response().withDelay(TimeUnit.SECONDS, 30));
        try {
            castlabsClient.login();
            fail("Expected exception");
        } catch (IOException e) {
            // ignore
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoginRequestGold() throws Exception {
        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets")
                .withBody("username=" + username + "&password=" + password)
                .withHeader("content-type", "application/x-www-form-urlencoded");
        String expectedTicket = "http://example.com";
        mockServerClient.when(loginRequest)
                .respond(response().withStatusCode(201).withHeader("location", expectedTicket));
        final String loginURL = castlabsClient.login();
        assertEquals(expectedTicket, loginURL);
        mockServerClient.verify(loginRequest, VerificationTimes.once());
    }

    @Test
    public void testTicketRequestWithLoginFailure() {
        String loginToken = "FVyaO9p22zQRdhPA47lUIhJ3WS0tDYsy47f2y7J2CAmGwR6X";
        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets")
                .withBody("username=" + username + "&password=" + password)
                .withHeader("content-type", "application/x-www-form-urlencoded");
        mockServerClient.when(loginRequest).respond(response().withStatusCode(401).withHeader(
                "location", "http://localhost:" + mockPort + "/cas/v1/tickets/" + loginToken));

        final HttpRequest ticketRequest =
                request().withMethod("POST").withPath("/cas/v1/tickets/" + loginToken);
        try {
            castlabsClient.getTicket("merchX");
            fail("Expected exception");
        } catch (CastlabsException e) {
            // ignore
        } catch (Exception e) {
            fail(e.getMessage());
        }
        mockServerClient.verify(loginRequest, VerificationTimes.once());
        mockServerClient.verify(ticketRequest, VerificationTimes.exactly(0));
    }

    @Test
    public void testUnauthorizedTicketRequest() {
        String loginToken = "FVyaO9p22zQRdhPA47lUIhJ3WS0tDYsy47f2y7J2CAmGwR6X";
        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets")
                .withBody("username=" + username + "&password=" + password)
                .withHeader("content-type", "application/x-www-form-urlencoded");
        mockServerClient.when(loginRequest).respond(response().withStatusCode(201).withHeader(
                "location", "http://localhost:" + mockPort + "/cas/v1/tickets/" + loginToken));

        final HttpRequest ticketRequest =
                request().withMethod("POST").withPath("/cas/v1/tickets/" + loginToken);
        mockServerClient.when(ticketRequest).respond(response().withStatusCode(401));
        try {
            castlabsClient.getTicket("merchX");
            fail("Expected exception");
        } catch (CastlabsException e) {
            // ignore
        } catch (Exception e) {
            fail(e.getMessage());
        }
        mockServerClient.verify(loginRequest, VerificationTimes.once());
        mockServerClient.verify(ticketRequest, VerificationTimes.once());
    }

    @Test
    public void testTicketRequestGold() throws Exception {
        String loginToken = "FVyaO9p22zQRdhPA47lUIhJ3WS0tDYsy47f2y7J2CAmGwR6X";
        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets")
                .withBody("username=" + username + "&password=" + password)
                .withHeader("content-type", "application/x-www-form-urlencoded");
        mockServerClient.when(loginRequest).respond(response().withStatusCode(201).withHeader(
                "location", "http://localhost:" + mockPort + "/cas/v1/tickets/" + loginToken));

        final HttpRequest ticketRequest =
                request().withMethod("POST").withPath("/cas/v1/tickets/" + loginToken);
        mockServerClient.when(ticketRequest).respond(
                response().withStatusCode(200).withBody("F40D6052-236F-4942-9C51-DD01C15A14C6"));
        final String ticket = castlabsClient.getTicket("merchX");
        assertNotNull(ticket);
        assertEquals("F40D6052-236F-4942-9C51-DD01C15A14C6", ticket);

        mockServerClient.verify(loginRequest, VerificationTimes.once());
        mockServerClient.verify(ticketRequest, VerificationTimes.once());
    }

    @Test
    public void testIngestKeysGold() throws Exception {
        final String merchantId = "merchX";
        final String ticket = "F40D6052-236F-4942-9C51-DD01C15A14C6";
        final String loginToken = "FVyaO9p22zQRdhPA47lUIhJ3WS0tDYsy47f2y7J2CAmGwR6X";
        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets")
                .withBody("username=" + username + "&password=" + password)
                .withHeader("content-type", "application/x-www-form-urlencoded");
        mockServerClient.when(loginRequest).respond(response().withStatusCode(201).withHeader(
                "location", "http://localhost:" + mockPort + "/cas/v1/tickets/" + loginToken));

        final HttpRequest ticketRequest =
                request().withMethod("POST").withPath("/cas/v1/tickets/" + loginToken);
        mockServerClient.when(ticketRequest)
                .respond(response().withStatusCode(200).withBody(ticket));

        final HttpRequest ingestAssetKeysRequest =
                request().withMethod("POST").withPath("/frontend/api/keys/v2/ingest/" + merchantId)
                        .withQueryStringParameter("ticket", ticket)
                        .withHeader("accept", "application/json")
                        .withHeader("content-type", "application/json");
        mockServerClient.when(ingestAssetKeysRequest).respond(response().withStatusCode(200).withBody("{\n" + 
                "  \"assets\": [\n" + 
                "    {\n" + 
                "      \"assetId\": \"rst_test_20150909_004\",\n" + 
                "      \"keys\": [\n" + 
                "        {\n" + 
                "          \"streamType\": \"VIDEO\",\n" + 
                "          \"keyRotationId\": \"1\",\n" + 
                "          \"keyId\": \"a6dHTVECSJe0MEJEOiZHQg==\"\n" + 
                "        }\n" + 
                "      ]\n" + 
                "    }\n" + 
                "  ]\n" + 
                "}"));
        final IngestKeysRequest ingestKeysRequest = new IngestKeysRequest();
        final Asset asset = new Asset();
        asset.setAssetId("rst_test_20150909_004");
        final IngestKey key = new IngestKey();
        key.setStreamType(StreamType.VIDEO);
        key.setKeyId("a6dHTVECSJe0MEJEOiZHQg==");
        key.setKeyRotationId("1");
        asset.getIngestKeys().add(key);
        ingestKeysRequest.getAssets().add(asset);
        final IngestKeysResponse ingestKeysResponse =
                castlabsClient.ingestKeys(ingestKeysRequest, merchantId);
        assertNotNull(ingestKeysResponse);
        assertEquals(asset.getAssetId(), asset.getAssetId());
        assertEquals(key.getKeyId(), asset.getIngestKeys().get(0).getKeyId());
        assertEquals(key.getKeyRotationId(), asset.getIngestKeys().get(0).getKeyRotationId());
        assertEquals(key.getStreamType(), asset.getIngestKeys().get(0).getStreamType());

        mockServerClient.verify(loginRequest, VerificationTimes.once());
        mockServerClient.verify(ticketRequest, VerificationTimes.once());
        mockServerClient.verify(ingestAssetKeysRequest, VerificationTimes.once());
    }

    @Test
    public void testIngestKeysErrorNotFound() {
        final String merchantId = "merchX";
        final String ticket = "F40D6052-236F-4942-9C51-DD01C15A14C6";
        final String loginToken = "FVyaO9p22zQRdhPA47lUIhJ3WS0tDYsy47f2y7J2CAmGwR6X";
        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets")
                .withBody("username=" + username + "&password=" + password)
                .withHeader("content-type", "application/x-www-form-urlencoded");
        mockServerClient.when(loginRequest).respond(response().withStatusCode(201).withHeader(
                "location", "http://localhost:" + mockPort + "/cas/v1/tickets/" + loginToken));

        final HttpRequest ticketRequest =
                request().withMethod("POST").withPath("/cas/v1/tickets/" + loginToken);
        mockServerClient.when(ticketRequest)
                .respond(response().withStatusCode(200).withBody(ticket));

        final HttpRequest ingestAssetKeysRequest =
                request().withMethod("POST").withPath("/frontend/api/keys/v2/ingest/" + merchantId)
                        .withQueryStringParameter("ticket", ticket)
                        .withHeader("accept", "application/json")
                        .withHeader("content-type", "application/json");
        mockServerClient.when(ingestAssetKeysRequest).respond(response().withStatusCode(401));
        final IngestKeysRequest ingestKeysRequest = new IngestKeysRequest();
        try {
            castlabsClient.ingestKeys(ingestKeysRequest, merchantId);
            fail("Expected exception");
        } catch (CastlabsException e) {
            // ignore
        } catch (Exception e) {
            fail(e.getMessage());
        }

        mockServerClient.verify(loginRequest, VerificationTimes.once());
        mockServerClient.verify(ticketRequest, VerificationTimes.once());
        mockServerClient.verify(ingestAssetKeysRequest, VerificationTimes.once());
    }
}
