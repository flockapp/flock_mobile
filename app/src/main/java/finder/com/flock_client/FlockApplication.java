package finder.com.flock_client;

import android.content.SharedPreferences;

import finder.com.flock_client.client.api.HttpClient;

/**
 * Created by Daniel on 3/6/16.
 */

public class FlockApplication extends android.app.Application {
    private String currToken;
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(getString(R.string.shared_preferences_file), MODE_PRIVATE);
        currToken = preferences.getString("token", null);
    }

    public void setCurrToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
        currToken = token;
    }

    public String getCurrToken() {
        return currToken;
    }
}
