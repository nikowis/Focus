package pl.nikowis.focus.ui.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.facebook.FacebookLikesLoader;
import pl.nikowis.focus.ui.facebook.FacebookSettings;
import pl.nikowis.focus.ui.instagram.InstagramSettings;

import static pl.nikowis.focus.ui.facebook.FacebookSettings.KEY_PREF_SELECTED_PAGES;

/**
 * Created by Nikodem on 3/24/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    private FacebookSettings facebookSettings;
    private InstagramSettings instagramSettings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        facebookSettings = new FacebookSettings(this);
        instagramSettings = new InstagramSettings(this);
    }

}
