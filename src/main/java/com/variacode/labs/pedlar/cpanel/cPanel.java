package com.variacode.labs.pedlar.cpanel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;

public class cPanel {

    private String _domain;
    private String _user;
    private String _password;
    private static cPanel _this;
    private static API _cAPI;

    private cPanel(String domain, String user, String password) {
        _cAPI = new API();
        _domain = domain;
        _user = user;
        _password = password;
    }

    /**
     *
     * @param domain
     * @param user
     * @param password
     * @return
     */
    public static cPanel getInstance(String domain, String user, String password) {
        if (_this == null) {
            _this = new cPanel(domain, user, password);
        }
        return _this;
    }

    /**
     *
     * @param domain
     * @param user
     * @param password
     * @return
     */
    public static cPanel newInstance(String domain, String user, String password) {
        _this = new cPanel(domain, user, password);
        return _this;
    }
    /* End Constructors */

    /* Start Setters/Getters */
    /**
     * @param username
     * @javadoc setUsername(username)
     *
     */
    public void setUsername(String username) {
        _user = username;
    }

    /**
     * @param password
     * @javadoc setPassword(password)
     *
     */
    public void setPassword(String password) {
        _password = password;
    }

    /**
     * @param domain
     * @javadoc setDomain(domain)
     *
     */
    public void setDomain(String domain) {
        _domain = domain;
    }

    /**
     * @return @javadoc getUsername(username)
     *
     */
    public String getUsername() {
        return _user;
    }

    /**
     * @return @javadoc getPassword(password)
     *
     */
    public String getPassword() {
        return _password;
    }

    /**
     * @return @javadoc getDomain(domain)
     *
     */
    public String getDomain() {
        return _domain;
    }
    /* End Setters/Getters */

    /**
     * login() Logs in with the set user/password
     *
     * @return
     */
    public boolean login() {
        return _cAPI.login(_user, _password, _domain);
    }

    /**
     * login(username, password) Logs in with the provided user/password and
     * domain from constructor
     *
     * @param username
     * @param password
     * @return
     */
    public boolean login(String username, String password) {
        return _cAPI.login(username, password, _domain);
    }

    /**
     * login(username, password, domain) Logs in with the provided
     * user/password/domain
     *
     * @param username
     * @param password
     * @param domain
     * @return
     */
    public boolean login(String username, String password, String domain) {
        return _cAPI.login(username, password, _domain);
    }

    /**
     *
     * @param module
     * @param params
     * @param function
     * @return
     */
    public String api2_get_request(String module, String function, Map<String, String> params) {
        String prms = "";
        for (Map.Entry<String, String> e : params.entrySet()) {
            prms = prms + "&" + e.getKey() + "=" + e.getValue();
        }
        HttpResponse response = _cAPI.postGet(_domain, "/json-api/cpanel?cpanel_jsonapi_module=" + module + "&cpanel_jsonapi_func=" + function + "&cpanel_jsonapi_apiversion=2&nocache=1" + prms);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            response.getEntity().writeTo(byteStream);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        return byteStream.toString();
    }
}
