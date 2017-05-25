package pl.nikowis.focus.ui.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
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

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Preference resetPin = findPreference(getContext().getString(R.string.key_pref__pin_reset));
        resetPin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                prefs.edit().remove(getContext().getString(R.string.key_pref_pin)).commit();
                startMainActivity();
                return true;
            }
        });
    }

    private void startMainActivity() {
        Intent i = new Intent(getActivity(), PinActivity.class);
        startActivity(i);
        getActivity().finish();
    }

}
