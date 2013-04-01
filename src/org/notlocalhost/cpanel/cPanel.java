package org.notlocalhost.cpanel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;

public class cPanel {
    private String _domain;
    private String _user;
    private String _password;
    private static cPanel _this;
    private static Api _cAPI;
    
    /* Start Constructors */
    private cPanel(String domain) {
        _cAPI = new Api();
        _domain = domain;
    }
    
    private cPanel(String domain, String user, String password) {
        _cAPI = new Api();
        _domain = domain;
        _user = user;
        _password = password;
    }
    
    private cPanel() {
        _cAPI = new Api();
    }
    
    public static cPanel getInstance() {
        if(_this == null) {
            _this = new cPanel();
        }
        return _this;
    }
    
    public static cPanel getInstance(String domain, String user, String password) {
        if(_this == null) {
            _this = new cPanel(domain, user, password);
        }
        return _this;
    }
    
    public static cPanel getInstance(String domain) {
        if(_this == null) {
            _this = new cPanel(domain);
        }
        return _this;
    }
    
    public static cPanel newInstance(String domain) {
        _this = new cPanel(domain);
        return _this;
    }
    
    public static cPanel newInstance(String domain, String user, String password) {
        _this = new cPanel(domain, user, password);
        return _this;
    }
    /* End Constructors */
    
    /* Start Setters/Getters */
    
    /** @javadoc
      * setUsername(username) 
     **/
    public void setUsername(String username) {
        _user = username;
    }

    /** @javadoc
     * setPassword(password) 
    **/
    public void setPassword(String password) {
        _password = password;
    }
    /** @javadoc
     * setDomain(domain) 
    **/
    public void setDomain(String domain) {
        _domain = domain;
    }
    /** @javadoc
     * getUsername(username) 
    **/
   public String getUsername() {
       return _user;
   }
   /** @javadoc
    * getPassword(password) 
   **/
   public String getPassword() {
       return _password;
   }
   /** @javadoc
    * getDomain(domain) 
   **/
   public String getDomain() {
       return _domain;
   }
   /* End Setters/Getters */
   
   /**
    * login()
    * Logs in with the set user/password
    */
   public boolean login() {
       return _cAPI.login(_user, _password, _domain);
   }
   /**
    * login(username, password)
    * Logs in with the provided user/password and domain from constructor
    */
   public boolean login(String username, String password) {
       return _cAPI.login(username, password, _domain);
   }
   /**
    * login(username, password, domain)
    * Logs in with the provided user/password/domain
    */
   public boolean login(String username, String password, String domain) {
       return _cAPI.login(username, password, _domain);
   }
   
   public String api2_get_request(PostData data) {
       String _module = data.getString("module");
       String _function = data.getString("function");
       HttpResponse response = _cAPI.postGet(_domain, "/json-api/cpanel?cpanel_jsonapi_module=" + _module + "&cpanel_jsonapi_func=" + _function + "&cpanel_jsonapi_apiversion=2&nocache=1");
       ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
       try {
           response.getEntity().writeTo(byteStream);
       } catch (IOException e) {
           e.printStackTrace();
       }
       String body = new String(byteStream.toString());
       return body;
   }
   public String api2_get_request(String module, String function) {
       HttpResponse response = _cAPI.postGet(_domain, "/json-api/cpanel?cpanel_jsonapi_module=" + module + "&cpanel_jsonapi_func=" + function + "&cpanel_jsonapi_apiversion=2&nocache=1");
       ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
       try {
           response.getEntity().writeTo(byteStream);
       } catch (IOException e) {
           e.printStackTrace();
       }
       String body = new String(byteStream.toString());
       return body;
   }
}