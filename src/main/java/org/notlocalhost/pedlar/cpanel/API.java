package org.notlocalhost.pedlar.cpanel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

public class API {

    private static HttpClient httpClient;
    private static boolean loggedIn;
    private static String httpCookie;
    private static MyRedirectHandler handler;
    private static boolean sessionId = false;

    public API() {
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        handler = new MyRedirectHandler();
        ((AbstractHttpClient) httpClient).setRedirectHandler(handler);
        loggedIn = false;
        httpCookie = "";
    }

    public boolean login(String username, String password, String domain) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("user", username));
        nameValuePairs.add(new BasicNameValuePair("pass", password));
        HttpResponse response = postData(domain, "login/", nameValuePairs);
        
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try {
            response.getEntity().writeTo(byteStream);
        } catch (IOException ex) {
            Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, ex);
        }

        String body = byteStream.toString();
        Matcher matcher = Pattern.compile("=/(.+)/front").matcher(body);
        if (matcher.find()) {
            httpCookie = matcher.group().replaceAll("\\/", "").replaceAll("front", "").replaceAll("=", "");
            loggedIn = true;
            sessionId = true;
        } else {
            Header[] headers = response.getHeaders("set-cookie");
            for (Header header : headers) {
                if (header.getValue().contains("session=")) {
                    httpCookie = header.getValue();
                    loggedIn = true;
                }
            }
        }
        return loggedIn;
    }

    public HttpResponse postData(String domain, String query, List<NameValuePair> nameValuePairs) {
        // Create a new HttpClient and Post Header
        HttpPost httppost = new HttpPost("http://" + domain + ":2082/" + (sessionId ? httpCookie : "") + query);
        try {
            if (httpCookie.length() > 0 && !sessionId) {
                httppost.setHeader("Cookie", httpCookie);
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            return httpClient.execute(httppost);
        } catch (ClientProtocolException e) {
            Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public HttpResponse postGet(String domain, String query) {
        // Create a new HttpClient and Post Header
        HttpGet httpget = new HttpGet("http://" + domain + ":2082/" + (sessionId ? httpCookie : "") + query);
        try {
            if (httpCookie.length() > 0 && !sessionId) {
                httpget.setHeader("Cookie", httpCookie);
            }
            return httpClient.execute(httpget);
        } catch (ClientProtocolException e) {
            Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public class MyRedirectHandler extends DefaultRedirectHandler {

        private URI lastRedirectedUri;

        @Override
        public URI getLocationURI(HttpResponse response, HttpContext context)
                throws ProtocolException {
            lastRedirectedUri = super.getLocationURI(response, context);
            return lastRedirectedUri;
        }
    }
}
