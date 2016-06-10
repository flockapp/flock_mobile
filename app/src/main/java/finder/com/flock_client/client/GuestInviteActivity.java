package finder.com.flock_client.client;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.User;

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

    private class GuestListAdapter extends ArrayAdapter<User> {

        public GuestListAdapter(Context context, int resource, List<User> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewHolder;
            if (convertView == null) {
                ViewHolder newViewHolder = new ViewHolder();
                newViewHolder.name = (TextView) parent.findViewById(R.id.text_guest_invite_name);
                convertView.setTag(newViewHolder);
            }
            mainViewHolder = (ViewHolder) convertView.getTag();
            mainViewHolder.name.setText(getItem(position).getFullName());
            return convertView;
        }

        private class ViewHolder {
            TextView name;
        }

    }
}
