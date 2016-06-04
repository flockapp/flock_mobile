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

    public User() throws Exception {}

    public User(String username, String password) throws Exception {
        this.username = username;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public boolean loginSuccess() {
        return loginSuccess;
    }

    public JSONObject login() throws Exception {
        HttpClient client = new HttpClient();
        JSONObject userInfo = new JSONObject();
        userInfo.put("username", username);
        userInfo.put("password", password);
        JSONObject resp = client.makePostRequest("/auth/login", userInfo);
        if (resp.getBoolean("success")) {
            loginSuccess = true;
            token = resp.getString("data");
        }
        Log.d("login debug", resp.getString("debug"));
        return resp;
    }

    public static JSONObject register(String fullName, String username, String password) throws Exception {
        HttpClient client = new HttpClient();
        JSONObject userInfo = new JSONObject();
        userInfo.put("username", username);
        userInfo.put("fullName", fullName);
        userInfo.put("password", password);
        return client.makePostRequest("/auth/register", userInfo);
    }
}
