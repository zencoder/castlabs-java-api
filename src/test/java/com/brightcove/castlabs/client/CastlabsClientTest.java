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

import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Scott Kidder
 *
 */
public class CastlabsClientTest {

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(1080, this);

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
        castlabsClient = new CastlabsClient(username, password, "http://localhost:1080", 1);
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
        mockServerClient.when(loginRequest).respond(response().withStatusCode(401)
                .withHeader("location", "http://localhost:1080/cas/v1/tickets/" + loginToken));

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
        mockServerClient.when(loginRequest).respond(response().withStatusCode(201)
                .withHeader("location", "http://localhost:1080/cas/v1/tickets/" + loginToken));

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
        mockServerClient.when(loginRequest).respond(response().withStatusCode(201)
                .withHeader("location", "http://localhost:1080/cas/v1/tickets/" + loginToken));

        final HttpRequest ticketRequest =
                request().withMethod("POST").withPath("/cas/v1/tickets/" + loginToken);
        mockServerClient.when(ticketRequest).respond(response().withStatusCode(200).withBody("F40D6052-236F-4942-9C51-DD01C15A14C6"));
        final String ticket = castlabsClient.getTicket("merchX");
        assertNotNull(ticket);
        assertEquals("F40D6052-236F-4942-9C51-DD01C15A14C6", ticket);

        mockServerClient.verify(loginRequest, VerificationTimes.once());
        mockServerClient.verify(ticketRequest, VerificationTimes.once());
    }

}
