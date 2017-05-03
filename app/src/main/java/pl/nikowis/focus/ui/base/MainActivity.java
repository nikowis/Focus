package pl.nikowis.focus.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

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

    private FacebookFragment facebookFragment;
    private InstagramFragment instagramFragment;
    private TwitterFragment twitterFragment;
    private GmailFragment gmailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TwitterRequestManager.CLIENT_ID, TwitterRequestManager.CLIENT_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        facebookFragment = new FacebookFragment();
        instagramFragment = new InstagramFragment();
        twitterFragment = new TwitterFragment();
        gmailFragment = new GmailFragment();

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
                    return facebookFragment;
                case INSTAGRAM:
                    return instagramFragment;
                case TWITTER:
                    return twitterFragment;
                case GMAIL:
                    return gmailFragment;
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
                    return getString(R.string.facebook_name);
                case INSTAGRAM:
                    return getString(R.string.instagram_name);
                case TWITTER:
                    return getString(R.string.twitter_name);
                case GMAIL:
                    return getString(R.string.gmail_name);
            }
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (twitterFragment != null) {
            twitterFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public enum Medias {
        FACEBOOK,
        INSTAGRAM,
        TWITTER,
        GMAIL;
    }
}
