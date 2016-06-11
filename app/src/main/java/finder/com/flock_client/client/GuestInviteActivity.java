package finder.com.flock_client.client;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.Guest;
import finder.com.flock_client.client.api.GuestList;
import finder.com.flock_client.client.api.User;

/**
 * Created by Daniel on 8/6/16.
 */

public class GuestInviteActivity extends AppCompatActivity {
    @BindView(R.id.list_guest_invite) public ListView guestInviteList;

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
        new FetchGuestListTask().execute();
    }

    private class GuestListAdapter extends ArrayAdapter<Guest> {
        private int layout;

        public GuestListAdapter(Context context, int resource, List<Guest> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewHolder;
            if (convertView == null) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                convertView = li.inflate(layout, parent, false);
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

    private class FetchGuestListTask extends AsyncTask<Void, Void, ArrayList<Guest>> {
        @Override
        protected ArrayList<Guest> doInBackground(Void... params) {
            GuestList guestList = new GuestList(((FlockApplication) getApplication()).getCurrToken(), eventId);
            try {
                return guestList.get();
            } catch (Exception e) {
                Log.d("debug", "error", e);
            }
            return null;
        }

        @Override
        public void onPostExecute(final ArrayList<Guest> resp) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (resp != null) {
                        GuestListAdapter gla = new GuestListAdapter(
                                getApplicationContext(),
                                R.layout.item_guest_invite,
                                resp);
                        guestInviteList.setAdapter(gla);
                        gla.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
