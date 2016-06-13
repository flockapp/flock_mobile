package finder.com.flock_client.client;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new VerifyUserTask().execute();

        //Firebase invites
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();

        // Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
        // would automatically launch the deep link if one is found.
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, false)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    //HANDLE INVITE
                                } else {
                                    Log.d("debug error", "getInvitation: no deep link found.");
                                }
                            }
                        });
    }

    //

    //Google API error handler
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("debug error", connectionResult.getErrorMessage());
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
