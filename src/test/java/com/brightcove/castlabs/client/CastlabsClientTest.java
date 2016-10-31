package com.brightcove.castlabs.client;

import static org.junit.Assert.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.brightcove.castlabs.client.request.*;
import com.brightcove.castlabs.client.response.AddSubMerchantAccountResponse;
import org.apache.commons.io.IOUtils;
import org.hamcrest.core.StringContains;
import org.junit.*;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;

import com.brightcove.castlabs.client.response.IngestAssetsResponse;

public class CastlabsClientTest {

    private static final int mockPort = 1080;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(mockPort, this);

    private MockServerClient mockServerClient;

    private CastlabsClient castlabsClient;

    private String username;
    private String password;

    final String exampleLoginToken = "FVyaO9p22zQRdhPA47lUIhJ3WS0tDYsy47f2y7J2CAmGwR6X";
    final String exampleTicket = "F40D6052-236F-4942-9C51-DD01C15A14C6";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        username = "x";
        password = "y";
        String mockURL = "http://localhost:" + mockPort;
        castlabsClient = new CastlabsClient(username, password, mockURL, mockURL, 1);

        final HttpRequest loginRequest = request().withMethod("POST").withPath("/cas/v1/tickets")
                .withBody("username=" + username + "&password=" + password)
                .withHeader("content-type", "application/x-www-form-urlencoded");
        mockServerClient.when(loginRequest).respond(response().withStatusCode(201).withHeader(
                "location", "http://localhost:" + mockPort + "/cas/v1/tickets/" + exampleLoginToken));

        final HttpRequest ticketRequest =
                request().withMethod("POST").withPath("/cas/v1/tickets/" + exampleLoginToken);
        mockServerClient.when(ticketRequest)
                .respond(response().withStatusCode(200).withBody(exampleTicket));
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
        mockServerClient.reset().when(loginRequest).respond(response().withStatusCode(401));
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
        mockServerClient.reset().when(loginRequest).respond(response().withStatusCode(201));
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
        mockServerClient.reset().when(loginRequest).respond(response().withDelay(TimeUnit.SECONDS, 30));
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
        mockServerClient.reset().when(loginRequest)
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
        String uri = "http://localhost:" + mockPort + "/cas/v1/tickets/" + loginToken;
        mockServerClient.reset().when(loginRequest).respond(response().withStatusCode(401).withHeader(
                "location", uri));

        final HttpRequest ticketRequest =
                request().withMethod("POST").withPath("/cas/v1/tickets/" + loginToken);
        try {
            castlabsClient.getUrlWithTicket(uri);
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

        mockServerClient
                .reset()
                .when(loginRequest)
                .respond(response().withStatusCode(201).withHeader(
                        "location",
                        "http://localhost:" + mockPort + "/cas/v1/tickets/" + loginToken
                ));

        final HttpRequest ticketRequest = request()
                .withMethod("POST")
                .withPath("/cas/v1/tickets/" + loginToken);

        mockServerClient
                .when(ticketRequest)
                .respond(response().withStatusCode(401));

        try {
            castlabsClient.getUrlWithTicket("merchX");
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
        final String ticketRequestUrl = "http://localhost:" + mockPort + "/cas/v1/tickets/" + loginToken;
        mockServerClient.reset().when(loginRequest).respond(response().withStatusCode(201).withHeader(
                "location", ticketRequestUrl));

        final HttpRequest ticketRequest =
                request().withMethod("POST").withPath("/cas/v1/tickets/" + loginToken);
        mockServerClient.when(ticketRequest).respond(
                response().withStatusCode(200).withBody("F40D6052-236F-4942-9C51-DD01C15A14C6"));

        final String urlWithTicket = castlabsClient.getUrlWithTicket(ticketRequestUrl);
        assertNotNull(urlWithTicket);
        assertEquals("http://localhost:1080/cas/v1/tickets/FVyaO9p22zQRdhPA47lUIhJ3WS0tDYsy47f2y7J2CAmGwR6X?ticket=F40D6052-236F-4942-9C51-DD01C15A14C6", urlWithTicket);

        mockServerClient.verify(loginRequest, VerificationTimes.once());
        mockServerClient.verify(ticketRequest, VerificationTimes.once());
    }

    @Test
    public void testIngestKeysGold() throws Exception {
        final String merchantId = "merchX";
        final HttpRequest ingestAssetKeysRequest =
                request().withMethod("POST").withPath("/frontend/api/keys/v2/ingest/" + merchantId)
                        .withQueryStringParameter("ticket", exampleTicket)
                        .withHeader("accept", "application/json")
                        .withHeader("content-type", "application/json");
        final String response = IOUtils.toString(new FileInputStream("src/test/resources/sample_ingest_response.json"));
        mockServerClient.when(ingestAssetKeysRequest).respond(response().withStatusCode(200).withBody(response));

        final AssetRequest asset = new AssetRequest();
        asset.setAssetId("rst_test_20150909_004");
        final IngestKey key = new IngestKey();
        key.setStreamType(StreamType.VIDEO);
        key.setKeyId("a6dHTVECSJe0MEJEOiZHQg==");
        key.setKeyRotationId("1");
        asset.getIngestKeys().add(key);
        final IngestKeysRequest ingestKeysRequest = new IngestKeysRequest();
        ingestKeysRequest.getAssets().add(asset);

        final IngestAssetsResponse ingestKeysResponse = castlabsClient.ingestKeys(ingestKeysRequest, merchantId);

        assertNotNull(ingestKeysResponse);
        assertEquals(asset.getAssetId(), asset.getAssetId());
        assertEquals(key.getKeyId(), asset.getIngestKeys().get(0).getKeyId());
        assertEquals(key.getKeyRotationId(), asset.getIngestKeys().get(0).getKeyRotationId());
        assertEquals(key.getStreamType(), asset.getIngestKeys().get(0).getStreamType());
        mockServerClient.verify(ingestAssetKeysRequest, VerificationTimes.once());
    }

    @Test
    public void testIngestKeysErrorNotFound() {
        final String merchantId = "merchX";

        final HttpRequest ingestAssetKeysRequest =
                request().withMethod("POST").withPath("/frontend/api/keys/v2/ingest/" + merchantId)
                        .withQueryStringParameter("ticket", exampleTicket)
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

        mockServerClient.verify(ingestAssetKeysRequest, VerificationTimes.once());
    }

    @Test
    public void testItCanMakeASubMerchantCreationRequest() throws Exception {
        final String merchantId = "merchX";
        final HttpRequest expectedRequest =
                request().withMethod("POST").withPath("/frontend/rest/reselling/v1/reseller/" + merchantId + "/submerchant/add")
                        .withQueryStringParameter("ticket", exampleTicket)
                        .withHeader("accept", "application/json")
                        .withHeader("content-type", "application/json");
        final String mockResponse = IOUtils.toString(new FileInputStream("src/test/resources/sample_sub_merchant_creation_response.json"));
        mockServerClient.when(expectedRequest).respond(response().withStatusCode(200).withBody(mockResponse));
        AddSubMerchantAccountRequest request = new AddSubMerchantAccountRequest();
        request.setApiNameSuffix("exampleAPINameSuffix");
        request.setLinkedUserUuid("10f38484-ff73-4101-b879-8794dd26fe77");
        request.setName("Example Name");

        final AddSubMerchantAccountResponse response = castlabsClient.addSubMerchantAccount(request, merchantId);

        assertNotNull(response);
        assertEquals(response.getSubMerchantUuid(), "0491caa4-8392-4b42-badc-f38a00134d99");
        mockServerClient.verify(expectedRequest, VerificationTimes.once());
    }

    @Test
    public void testItCanHandleCastlabsErrorsWhenMakingASubMerchantCreationRequest() throws Exception {
        final String merchantId = "merchX";
        final HttpRequest expectedRequest =
                request().withMethod("POST").withPath("/frontend/rest/reselling/v1/reseller/" + merchantId + "/submerchant/add")
                        .withQueryStringParameter("ticket", exampleTicket)
                        .withHeader("accept", "application/json")
                        .withHeader("content-type", "application/json");
        final String mockResponse = IOUtils.toString(new FileInputStream("src/test/resources/sample_sub_merchant_creation_error_response.json"));
        mockServerClient.when(expectedRequest).respond(response().withStatusCode(200).withBody(mockResponse));
        AddSubMerchantAccountRequest request = new AddSubMerchantAccountRequest();
        request.setApiNameSuffix("exampleAPINameSuffix");
        request.setName("Example Name");

        try {
            castlabsClient.addSubMerchantAccount(request, merchantId);
            fail("Expected a CastlabsException to be returned");
        } catch(CastlabsException e) {
            assertThat(e.getMessage(), StringContains.containsString("linkedUserUuid"));
            assertThat(e.getMessage(), StringContains.containsString("may not be null"));
            mockServerClient.verify(expectedRequest, VerificationTimes.once());
        }
    }

    @Test
    public void testItCanHandleHttpErrorsWhenMakingASubMerchantCreationRequest() throws Exception {
        final String merchantId = "merchX";
        final HttpRequest expectedRequest =
                request().withMethod("POST").withPath("/frontend/rest/reselling/v1/reseller/" + merchantId + "/submerchant/add")
                        .withQueryStringParameter("ticket", exampleTicket)
                        .withHeader("accept", "application/json")
                        .withHeader("content-type", "application/json");
        mockServerClient.when(expectedRequest).respond(response().withStatusCode(400));
        AddSubMerchantAccountRequest request = new AddSubMerchantAccountRequest();
        request.setApiNameSuffix("exampleAPINameSuffix");
        request.setLinkedUserUuid("10f38484-ff73-4101-b879-8794dd26fe77");
        request.setName("Example Name");

        try {
            castlabsClient.addSubMerchantAccount(request, merchantId);
            fail("Expected a CastlabsException to be returned");
        } catch(CastlabsException e) {
            assertThat(e.getMessage(), StringContains.containsString("HTTP Status: 400"));
            mockServerClient.verify(expectedRequest, VerificationTimes.once());
        }
    }

}
