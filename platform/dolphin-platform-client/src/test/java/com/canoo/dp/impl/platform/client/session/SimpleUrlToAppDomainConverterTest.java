package com.canoo.dp.impl.platform.client.session;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;

public class SimpleUrlToAppDomainConverterTest {

    private SimpleUrlToAppDomainConverter simpleUrlToAppDomainConverter;

    private static final String PROTOCOL = "http";
    private static final String HOST = "example.com";
    private static final int PORT = 80;

    @BeforeMethod
    public void setUp() throws Exception {
        simpleUrlToAppDomainConverter = new SimpleUrlToAppDomainConverter();
    }

    @Test
    public void getApplicationDomain_whenGivenAURL_thenConvertAppDomainCorrectly() throws Exception {
        // GIVEN
        URL url = new URL(PROTOCOL, HOST, PORT, "");

        // WHEN
        String result = simpleUrlToAppDomainConverter.getApplicationDomain(url).orElseThrow(() -> new RuntimeException("ERROR"));

        // THEN
        Assert.assertEquals(result, HOST + ":" + PORT);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void getApplicationDomain_whenGivenANullURL_thenThrownANullPointerException() throws Exception {
        // WHEN
        simpleUrlToAppDomainConverter.getApplicationDomain(null);
    }
}