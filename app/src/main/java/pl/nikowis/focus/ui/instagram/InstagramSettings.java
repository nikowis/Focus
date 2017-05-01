package pl.nikowis.focus.ui.instagram;

import android.content.Context;

import pl.nikowis.focus.ui.base.SettingsFragment;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class InstagramSettings {

    /**
     * Preference key for adding new custom pages.
     */
    public static final String KEY_PREF_INSTAGRAM_AUTH_TOKEN = "pref_instagram_auth_token";

    private Context context;
    private SettingsFragment fragment;

    public InstagramSettings(SettingsFragment settingsFragment) {
        this.fragment = settingsFragment;
        this.context = fragment.getContext();
    }
}
