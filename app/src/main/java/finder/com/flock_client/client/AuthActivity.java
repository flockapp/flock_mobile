package finder.com.flock_client.client;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


import java.util.ArrayList;

import finder.com.flock_client.R;
import finder.com.flock_client.fragments.LoginFragment;
import finder.com.flock_client.fragments.RegisterFragment;

public class AuthActivity extends AppCompatActivity {

    private AuthPageAdapter mAuthPageAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;


    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.activity_auth);

        mAuthPageAdapter =
                new AuthPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.activity_auth_pager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_auth);

        mViewPager.setAdapter(mAuthPageAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public class AuthPageAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Pair<String, Fragment>> fList = new ArrayList<>();

        public AuthPageAdapter(FragmentManager fm) {
            super(fm);
            fList.add(0, new Pair<String, Fragment>("Login", new LoginFragment()));
            fList.add(1, new Pair<String, Fragment>("Register", new RegisterFragment()));
        }

        @Override
        public Fragment getItem(int position) {
            return fList.get(position).second;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int pos) {
            return fList.get(pos).first;
        }
    }
}