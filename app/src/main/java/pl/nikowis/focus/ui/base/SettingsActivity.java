package pl.nikowis.focus.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Nikodem on 3/24/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
