package pl.nikowis.focus.ui.gmail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.base.MainActivity;
import pl.nikowis.focus.ui.base.SettingsFragment;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class GmailSettings {

    private Context context;
    private SettingsFragment fragment;
    private boolean loggedIn;

    public GmailSettings(SettingsFragment settingsFragment) {
        this.fragment = settingsFragment;
        this.context = fragment.getContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Preference gmailLogout = settingsFragment.findPreference(context.getString(R.string.key_pref_gmail_logout));
        loggedIn = prefs.getBoolean(context.getString(R.string.key_pref_gmail_logged_in), false);
        Preference pageCount = settingsFragment.findPreference(context.getString(R.string.key_pref_gmail_page_count));
        pageCount.setEnabled(loggedIn);
        gmailLogout.setEnabled(loggedIn);
        gmailLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                prefs.edit().remove(context.getString(R.string.key_pref_gmail_logged_in)).apply();
                prefs.edit().remove(context.getString(R.string.key_pref_gmail_account_name)).apply();
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
