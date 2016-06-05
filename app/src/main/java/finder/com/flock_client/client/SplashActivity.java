package finder.com.flock_client.client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String token = ((FlockApplication) getApplication()).getCurrToken();
        if (token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DashActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
