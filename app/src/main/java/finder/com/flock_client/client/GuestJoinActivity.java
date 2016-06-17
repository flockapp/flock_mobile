package finder.com.flock_client.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;

public class GuestJoinActivity extends AppCompatActivity {
    @BindView(R.id.input_event_code) public EditText eventCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_join);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_join_event)
    public void OnJoinEventButtonClicked() {

    }
}
