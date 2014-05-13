package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;

/**
 * Tests the WebServiceClient class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WebServiceClientTest {
    private static final String GET_URL = "http://www.google.co.uk";

    // This is a POST data echo service
    private static final String POST_URL = "http://httpbin.org/post";

    @Test
    public void makeGetRequestThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makeGetRequest("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makeGetRequestThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makeGetRequest("this is malformed");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makeGetRequestThrowsExceptionIfUnknownPage() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makeGetRequest("http://www.google.co.uk/kjhdfgoiunewrpoimclsd");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makeGetRequestSuccessfullyGetsValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        String response = client.makeGetRequest(GET_URL);

        // Assert
        assertThat(response).containsIgnoringCase("google");
    }

    @Test
    public void makePostRequestThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePostRequestWithJSON("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be", null);

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePostRequestWithJSON("this is malformed", null);

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestSuccessfullyPostsToValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();
        String name = "Harry Hill";

        // Act
        String json = "{ \"name\": \"" + name + "\", \"age\": 49, \"dateOfBirth\": \"1964-10-01\" }";
        String response = client.makePostRequestWithJSON(POST_URL, json);

        // Assert
        assertThat(response).containsIgnoringCase(name);
    }
}
