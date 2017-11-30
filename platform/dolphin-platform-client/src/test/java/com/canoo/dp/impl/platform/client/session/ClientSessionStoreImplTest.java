package com.canoo.dp.impl.platform.client.session;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;

public class ClientSessionStoreImplTest {
    private ClientSessionStoreImpl implement;
    private String clientID;

    @Test
    public void getClientIdentifierForUrlWhenGivenAURL() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        URL url = new URL("http://example.com/80");
        clientID = "dolphin";
        implement.setClientIdentifierForUrl(url, clientID);
        //when
        final String result = implement.getClientIdentifierForUrl(url).orElseThrow(() -> new RuntimeException("ERROR"));
        //then
        Assert.assertEquals(result, clientID);
    }

    @Test
    public void setClientIdentifierForUrl() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        //when
        URL url = new URL("http://example.com/80");
        String clientID = "dolphin";
        implement.setClientIdentifierForUrl(url, clientID);
        //then
        final String setUrl = implement.getClientIdentifierForUrl(url).orElseThrow(() -> new RuntimeException("ERROR"));
        Assert.assertEquals(setUrl, clientID);
    }

    @Test
    public void getClientIdentifierForUrlWhenGiven2URL() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        URL url = new URL("http://amazon.com/80");
        URL url1 = new URL("http://apple.com/80");

        clientID = "amzn";
        String clientID1 = "appl";

        implement.setClientIdentifierForUrl(url, clientID);
        implement.setClientIdentifierForUrl(url1, clientID1);

        //when
        final String result = implement.getClientIdentifierForUrl(url).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result1 = implement.getClientIdentifierForUrl(url1).orElseThrow(() -> new RuntimeException("ERROR"));
        //then
        Assert.assertEquals(result, clientID);
        Assert.assertEquals(result1, clientID1);
    }

    @Test
    public void setClientIdentifierFor2Url() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        //when
        URL url = new URL("http://amazon.com/80");
        URL url1 = new URL("http://apple.com/80");

        String clientID = "amzn";
        String clientID1 = "appl";

        implement.setClientIdentifierForUrl(url, clientID);
        implement.setClientIdentifierForUrl(url1, clientID1);

        //then
        final String setUrl = implement.getClientIdentifierForUrl(url).orElseThrow(() -> new RuntimeException("ERROR"));
        final String setUrl1 = implement.getClientIdentifierForUrl(url1).orElseThrow(() -> new RuntimeException("ERROR"));

        Assert.assertEquals(setUrl, clientID);
        Assert.assertEquals(setUrl1, clientID1);
    }

    @Test
    public void getClientIdentifierForUrlWhenGiven10URL() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        URL url = new URL("http://amazon.com/80");
        URL url1 = new URL("http://apple.com/80");
        URL url2 = new URL("http://microsoft.com/80");
        URL url3 = new URL("http://google.com/80");
        URL url4 = new URL("http://yahoo.com/80");
        URL url5 = new URL("http://netflix.com/80");
        URL url6 = new URL("http://youtube.com/80");
        URL url7 = new URL("http://9to5mac.com/80");
        URL url8 = new URL("http://github.com/80");
        URL url9 = new URL("http://facebook.com/80");

        clientID = "amzn";
        String clientID1 = "appl";
        String clientID2 = "mfst";
        String clientID3 = "googl";
        String clientID4 = "yhoo";
        String clientID5 = "ntflx";
        String clientID6 = "ytub";
        String clientID7 = "9to5";
        String clientID8 = "gthb";
        String clientID9 = "";

        implement.setClientIdentifierForUrl(url, clientID);
        implement.setClientIdentifierForUrl(url1, clientID1);
        implement.setClientIdentifierForUrl(url2, clientID2);
        implement.setClientIdentifierForUrl(url3, clientID3);
        implement.setClientIdentifierForUrl(url4, clientID4);
        implement.setClientIdentifierForUrl(url5, clientID5);
        implement.setClientIdentifierForUrl(url6, clientID6);
        implement.setClientIdentifierForUrl(url7, clientID7);
        implement.setClientIdentifierForUrl(url8, clientID8);
        implement.setClientIdentifierForUrl(url9, clientID9);


        //when
        final String result = implement.getClientIdentifierForUrl(url).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result1 = implement.getClientIdentifierForUrl(url1).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result2 = implement.getClientIdentifierForUrl(url2).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result3 = implement.getClientIdentifierForUrl(url3).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result4 = implement.getClientIdentifierForUrl(url4).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result5 = implement.getClientIdentifierForUrl(url5).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result6 = implement.getClientIdentifierForUrl(url6).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result7 = implement.getClientIdentifierForUrl(url7).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result8 = implement.getClientIdentifierForUrl(url8).orElseThrow(() -> new RuntimeException("ERROR"));
        final String result9 = implement.getClientIdentifierForUrl(url9).orElseThrow(() -> new RuntimeException("ERROR"));
        //then
        Assert.assertEquals(result, clientID);
        Assert.assertEquals(result1, clientID1);
        Assert.assertEquals(result2, clientID2);
        Assert.assertEquals(result3, clientID3);
        Assert.assertEquals(result4, clientID4);
        Assert.assertEquals(result5, clientID5);
        Assert.assertEquals(result6, clientID6);
        Assert.assertEquals(result7, clientID7);
        Assert.assertEquals(result8, clientID8);
        Assert.assertEquals(result9, clientID9);
    }

    @Test
    public void setClientIdentifierFor10Url() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        //when
        URL url = new URL("http://amazon.com/80");
        URL url1 = new URL("http://apple.com/80");
        URL url2 = new URL("http://microsoft.com/80");
        URL url3 = new URL("http://google.com/80");
        URL url4 = new URL("http://yahoo.com/80");
        URL url5 = new URL("http://netflix.com/80");
        URL url6 = new URL("http://youtube.com/80");
        URL url7 = new URL("http://9to5mac.com/80");
        URL url8 = new URL("http://github.com/80");
        URL url9 = new URL("http://facebook.com/80");

        clientID = "amzn";
        String clientID1 = "appl";
        String clientID2 = "mfst";
        String clientID3 = "googl";
        String clientID4 = "yhoo";
        String clientID5 = "ntflx";
        String clientID6 = "ytub";
        String clientID7 = "9to5";
        String clientID8 = "gthb";
        String clientID9 = "";

        implement.setClientIdentifierForUrl(url, clientID);
        implement.setClientIdentifierForUrl(url1, clientID1);
        implement.setClientIdentifierForUrl(url2, clientID2);
        implement.setClientIdentifierForUrl(url3, clientID3);
        implement.setClientIdentifierForUrl(url4, clientID4);
        implement.setClientIdentifierForUrl(url5, clientID5);
        implement.setClientIdentifierForUrl(url6, clientID6);
        implement.setClientIdentifierForUrl(url7, clientID7);
        implement.setClientIdentifierForUrl(url8, clientID8);
        implement.setClientIdentifierForUrl(url9, clientID9);

        //then
        final String set = implement.getClientIdentifierForUrl(url).orElseThrow(() -> new RuntimeException("ERROR"));
        final String set1 = implement.getClientIdentifierForUrl(url1).orElseThrow(() -> new RuntimeException("ERROR"));
        final String set2 = implement.getClientIdentifierForUrl(url2).orElseThrow(() -> new RuntimeException("ERROR"));
        final String set3 = implement.getClientIdentifierForUrl(url3).orElseThrow(() -> new RuntimeException("ERROR"));
        final String set4 = implement.getClientIdentifierForUrl(url4).orElseThrow(() -> new RuntimeException("ERROR"));
        final String set5 = implement.getClientIdentifierForUrl(url5).orElseThrow(() -> new RuntimeException("ERROR"));
        final String set6 = implement.getClientIdentifierForUrl(url6).orElseThrow(() -> new RuntimeException("ERROR"));
        final String set7 = implement.getClientIdentifierForUrl(url7).orElseThrow(() -> new RuntimeException("ERROR"));
        final String set8 = implement.getClientIdentifierForUrl(url8).orElseThrow(() -> new RuntimeException("ERROR"));
        final String set9 = implement.getClientIdentifierForUrl(url9).orElseThrow(() -> new RuntimeException("ERROR"));

        Assert.assertEquals(set, clientID);
        Assert.assertEquals(set1, clientID1);
        Assert.assertEquals(set2, clientID2);
        Assert.assertEquals(set3, clientID3);
        Assert.assertEquals(set4, clientID4);
        Assert.assertEquals(set5, clientID5);
        Assert.assertEquals(set6, clientID6);
        Assert.assertEquals(set7, clientID7);
        Assert.assertEquals(set8, clientID8);
        Assert.assertEquals(set9, clientID9);
    }


    @Test
    public void TestSessionIsReset() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        //when
        URL url = new URL("http://example.com/80");
        implement.resetSession(url);

        //then
        final String setUrl = implement.getClientIdentifierForUrl(url).orElse(null);
        Assert.assertEquals(setUrl, null);

    }

    @Test
    public  void TestSessionIsResetForMoreThanOn1() throws Exception{
        implement = new ClientSessionStoreImpl();

        URL url1 = new URL("http://apple.com/80");
        URL url2 = new URL("http://microsoft.com/80");
        URL url3 = new URL("http://google.com/80");
        URL url4 = new URL("http://yahoo.com/80");
        URL url5 = new URL("http://netflix.com/80");


        implement.resetSession(url1);
        implement.resetSession(url2);
        implement.resetSession(url3);
        implement.resetSession(url4);
        implement.resetSession(url5);

        final String reset1 = implement.getClientIdentifierForUrl(url1).orElse(null);
        final String reset2 = implement.getClientIdentifierForUrl(url2).orElse(null);
        final String reset3 = implement.getClientIdentifierForUrl(url3).orElse(null);
        final String reset4 = implement.getClientIdentifierForUrl(url4).orElse(null);
        final String reset5 = implement.getClientIdentifierForUrl(url5).orElse(null);

        Assert.assertEquals(reset1, null);
        Assert.assertEquals(reset2, null);
        Assert.assertEquals(reset3, null);
        Assert.assertEquals(reset4, null);
        Assert.assertEquals(reset5, null);


    }
}
