package pl.nikowis.focus.ui.base;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.facebook.FacebookSettings;
import pl.nikowis.focus.ui.gmail.GmailSettings;
import pl.nikowis.focus.ui.instagram.InstagramSettings;
import pl.nikowis.focus.ui.twitter.TwitterSettings;

/**
 * Created by Nikodem on 3/24/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    public static final int MIN_PAGE_COUNT = 1;
    public static final int MAX_PAGE_COUNT = 20;
    private FacebookSettings facebookSettings;
    private InstagramSettings instagramSettings;
    private TwitterSettings twitterSettings;
    private GmailSettings gmailSettings;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        facebookSettings = new FacebookSettings(this);
        instagramSettings = new InstagramSettings(this);
        twitterSettings = new TwitterSettings(this);
        gmailSettings = new GmailSettings(this);
    }

}
