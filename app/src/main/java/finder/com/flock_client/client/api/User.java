package finder.com.flock_client.client.api;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Daniel on 2/6/16.
 */

public class User {

    private String token;
    private boolean loginSuccess = false;
    private String username;
    private String password;
    private String fullName;
    private HttpClient client;

    public User(String username, String password) throws Exception {
        this.username = username;
        this.password = password;
        client = new HttpClient();
    }

    public User(String username, String password, String fullName) throws Exception {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        client = new HttpClient();
    }

    public String getToken() {
        return token;
    }

    public boolean loginSuccess() {
        return loginSuccess;
    }

    public JSONObject login() throws Exception {
        JSONObject userInfo = new JSONObject();
        userInfo.put("username", username);
        userInfo.put("password", password);
        JSONObject resp = client.makePostRequest("/auth/login", userInfo);
        if (resp.getBoolean("success")) {
            loginSuccess = true;
            token = resp.getString("data");
        }
        return resp;
    }

    public JSONObject register() throws Exception {
        JSONObject userInfo = new JSONObject();
        userInfo.put("username", username);
        userInfo.put("fullName", fullName);
        userInfo.put("password", password);
        return client.makePostRequest("/auth/register", userInfo);
    }

    public String getFullName() {
        return fullName;
    }
}
