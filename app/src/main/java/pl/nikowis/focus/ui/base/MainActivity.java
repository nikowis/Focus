package pl.nikowis.focus.ui.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.twitter.TwitterRequestManager;
import pl.nikowis.focus.ui.facebook.FacebookFragment;
import pl.nikowis.focus.ui.gmail.GmailFragment;
import pl.nikowis.focus.ui.instagram.InstagramFragment;
import pl.nikowis.focus.ui.twitter.TwitterFragment;

public class MainActivity extends AppCompatActivity {

    private TabsPagerAdapter mTabsPagerAdapter;

    @BindView(R.id.tab_container)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TwitterRequestManager.CLIENT_ID, TwitterRequestManager.CLIENT_SECRET);
        Fabric.with(this, new TwitterCore(authConfig));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (Medias.values()[position]) {
                case FACEBOOK:
                    return new FacebookFragment();
                case INSTAGRAM:
                    return new InstagramFragment();
                case TWITTER:
                    return new TwitterFragment();
                case GMAIL:
                    return new GmailFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (Medias.values()[position]) {
                case FACEBOOK:
                    return "Facebook";
                case INSTAGRAM:
                    return "Instagram";
                case TWITTER:
                    return "Twitter";
                case GMAIL:
                    return "Gmail";
            }
            return null;
        }
    }

    public enum Medias {
        FACEBOOK,
        INSTAGRAM,
        TWITTER,
        GMAIL;
    }
}
