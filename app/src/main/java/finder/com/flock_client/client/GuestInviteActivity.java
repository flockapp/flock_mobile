package finder.com.flock_client.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;
import finder.com.flock_client.R;

/**
 * Created by Daniel on 8/6/16.
 */

public class GuestInviteActivity extends AppCompatActivity {

    private int eventId;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_guest_invite);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        eventId = extras.getInt("eventId", -1);
        if (eventId == -1) {
            Log.d("debug", "error eventId not found");
        }
        if (extras.getBoolean("first", false)) {
            //Add first-time text
        }

    }
}
