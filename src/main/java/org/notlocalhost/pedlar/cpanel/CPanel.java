package org.notlocalhost.pedlar.cpanel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;

public class CPanel {

    private String domain;
    private String user;
    private String password;
    private static CPanel _this;
    private final API cAPI;

    private CPanel(String domain, String user, String password) {
        this.cAPI = new API();
        this.domain = domain;
        this.user = user;
        this.password = password;
    }

    /**
     *
     * @param domain
     * @param user
     * @param password
     * @return
     */
    public static CPanel getInstance(String domain, String user, String password) {
        if (_this == null) {
            _this = new CPanel(domain, user, password);
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
    public static CPanel newInstance(String domain, String user, String password) {
        _this = new CPanel(domain, user, password);
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
        this.user = username;
    }

    /**
     * @param password
     * @javadoc setPassword(password)
     *
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param domain
     * @javadoc setDomain(domain)
     *
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return @javadoc getUsername(username)
     *
     */
    public String getUsername() {
        return this.user;
    }

    /**
     * @return @javadoc getPassword(password)
     *
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @return @javadoc getDomain(domain)
     *
     */
    public String getDomain() {
        return this.domain;
    }
    /* End Setters/Getters */

    /**
     * login() Logs in with the set user/password
     *
     * @return
     */
    public boolean login() {
        return this.cAPI.login(this.user, this.password, this.domain);
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
        return this.cAPI.login(username, password, this.domain);
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
        return this.cAPI.login(username, password, domain);
    }

    /**
     *
     * @param module
     * @param params
     * @param function
     * @return
     */
    public String api2GetRequest(String module, String function, Map<String, String> params) {
        String prms = "";
        for (Map.Entry<String, String> e : params.entrySet()) {
            prms = prms + "&" + e.getKey() + "=" + e.getValue();
        }
        HttpResponse response = this.cAPI.postGet(this.domain, "/json-api/cpanel?cpanel_jsonapi_module=" + module + "&cpanel_jsonapi_func=" + function + "&cpanel_jsonapi_apiversion=2&nocache=1" + prms);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            response.getEntity().writeTo(byteStream);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        return byteStream.toString();
    }
}
