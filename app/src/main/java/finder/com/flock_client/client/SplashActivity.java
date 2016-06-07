package finder.com.flock_client.client;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new VerifyUserTask().execute();
    }

    private class VerifyUserTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String token = ((FlockApplication) getApplication()).getCurrToken();
            return !token.equals("") && ((FlockApplication) getApplication()).verifyToken();
        }

        @Override
        public void onPostExecute(Boolean success) {
            if (success) {
                Intent intent = new Intent(getBaseContext(), DashActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
