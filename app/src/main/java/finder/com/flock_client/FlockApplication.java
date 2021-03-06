package finder.com.flock_client;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import finder.com.flock_client.client.api.HttpClient;

/**
 * Created by Daniel on 3/6/16.
 */

public class FlockApplication extends android.app.Application {
    private String currToken;
    private SharedPreferences preferences;
    private HttpClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(getString(R.string.shared_preferences_file), MODE_PRIVATE);
        currToken = preferences.getString("token", "");
        client = new HttpClient(currToken);
    }

    public void setCurrToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
        currToken = token;
        client.setToken(currToken);
    }

    public boolean verifyToken() {
        try {
            JSONObject resp = client.makeGetRequest("/v0/api/verify");
            if (resp.getBoolean("success")) {
                return true;
            }
        } catch (Exception e) {
            Log.d("debug", "error", e);
            return false;
        }
        return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean networkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!networkAvailable) {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }
        return networkAvailable;
    }

    public String getCurrToken() {
        return currToken;
    }
}
