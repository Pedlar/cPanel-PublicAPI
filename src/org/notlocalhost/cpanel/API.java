package org.notlocalhost.cpanel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.conn.ssl.SSLSocketFactory;

class Api {
    static private HttpClient httpClient;
    static private boolean logged_in;
    static private String httpCookie;
    static private MyRedirectHandler handler;
    static private boolean session_id = false;
    
    public Api() {
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        
        handler = new MyRedirectHandler();
        ((AbstractHttpClient) httpClient).setRedirectHandler(handler);
        
        logged_in = false;
        httpCookie = "";
    }
    
    public boolean login(String username, String password, String domain) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("user", username));
        nameValuePairs.add(new BasicNameValuePair("pass", password));
        HttpResponse response = postData(domain, "login/", nameValuePairs);
        
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            response.getEntity().writeTo(byteStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String body = new String(byteStream.toString());
        Matcher matcher = Pattern.compile("\\/cpsess\\d+\\/")
                                 .matcher(handler.lastRedirectedUri.toString());
        if(matcher.find()) {
            httpCookie = matcher.group().replaceAll("\\/", "");
            logged_in = true;
            session_id = true;
            //Log.d("cPanel-PublicAPI", httpCookie);
        } else {
            Header[] headers = response.getHeaders("set-cookie");
            for(Header header : headers) {
                //Log.d("cPanel-PublicAPI", header.getName() + ": " + header.getValue());
                if(header.getValue().contains("session=")) {
                   httpCookie = header.getValue();
                   logged_in = true;
                }
            }
        }
        return logged_in;
    }
    
    public HttpResponse postData(String domain, String query, List<NameValuePair> nameValuePairs) {
        // Create a new HttpClient and Post Header
        HttpPost httppost = new HttpPost("http://" + domain + ":2082/" + (session_id ? httpCookie : "") + query);
        try {
            if(httpCookie.length() > 0 && !session_id)
                httppost.setHeader("Cookie", httpCookie);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httppost);
            return response;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            //Log.e("cPanel-PublicAPI", "Caught Exception");
            e.printStackTrace();
        } catch (IOException e) {
            //Log.e("cPanel-PublicAPI", "Caught Exception");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public HttpResponse postGet(String domain, String query) {
        // Create a new HttpClient and Post Header
        HttpGet httpget = new HttpGet("http://" + domain + ":2082/" + (session_id ? httpCookie : "") + query);
        try {
            if(httpCookie.length() > 0 && !session_id)
                httpget.setHeader("Cookie", httpCookie);
            HttpResponse response = httpClient.execute(httpget);
            return response;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            //Log.e("cPanel-PublicAPI", "Caught Exception");
            e.printStackTrace();
        } catch (IOException e) {
            //Log.e("cPanel-PublicAPI", "Caught Exception");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean isLoggedIn() {
        return logged_in;
    }
    
    public class MyRedirectHandler extends DefaultRedirectHandler {
        public URI lastRedirectedUri;

        @Override
        public boolean isRedirectRequested(HttpResponse response, HttpContext context) {

            return super.isRedirectRequested(response, context);
        }

        @Override
        public URI getLocationURI(HttpResponse response, HttpContext context)
                throws ProtocolException {

            lastRedirectedUri = super.getLocationURI(response, context);

            return lastRedirectedUri;
        }
    }
}