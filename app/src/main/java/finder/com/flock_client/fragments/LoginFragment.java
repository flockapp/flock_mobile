package finder.com.flock_client.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finder.com.flock_client.R;


/**
 * Created by Daniel on 3/6/16.
 */

public class LoginFragment extends Fragment {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_auth_login, viewGroup, false);
    }
}
