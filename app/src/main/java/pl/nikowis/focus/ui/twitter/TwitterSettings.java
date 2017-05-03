package pl.nikowis.focus.ui.twitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.base.MainActivity;
import pl.nikowis.focus.ui.base.SettingsFragment;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class TwitterSettings {

    private Context context;
    private SettingsFragment fragment;
    private String authorizationToken;

    public TwitterSettings(SettingsFragment settingsFragment) {
        this.fragment = settingsFragment;
        this.context = fragment.getContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Preference twitterLogout = settingsFragment.findPreference(context.getString(R.string.key_pref_twitter_logout));

        authorizationToken = prefs.getString(context.getString(R.string.key_pref_twitter_auth_token), null);
        boolean userLoggedIn = authorizationToken != null;
        Preference pageCount = settingsFragment.findPreference(context.getString(R.string.key_pref_twitter_page_count));
        pageCount.setEnabled(userLoggedIn);
        twitterLogout.setEnabled(userLoggedIn);
        twitterLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                prefs.edit().remove(context.getString(R.string.key_pref_twitter_auth_token)).apply();
                prefs.edit().remove(context.getString(R.string.key_pref_twitter_auth_token_secret)).apply();
                navigateToMainActivity();
                return true;
            }
        });

    }

    private void navigateToMainActivity() {
        Intent i = new Intent(context, MainActivity.class);
        fragment.startActivity(i);
        fragment.getActivity().finish();
    }

}
