package com.eventjuggler.test;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;

public class HttpUtils {

    public static final String baseUrl = "http://localhost:8080";

    public static String get(String resource) throws Exception {
        URL url = new URL(baseUrl + resource);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            return IOUtils.toString(connection.getInputStream());
        } finally {
            connection.disconnect();
        }
    }

    public static <T> T get(String resource, TypeReference<T> typeRef) throws Exception {
        String response = get(resource);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, typeRef);
    }

}
