package pl.nikowis.focus.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.facebook.FacebookLikesLoader;

public class SplashActivity extends AppCompatActivity {

    private FacebookLikesLoader likesLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        likesLoader = new FacebookLikesLoader(getApplicationContext(), new FacebookLikesLoader.FinishedLoadingListener() {
            @Override
            public void finished() {
                startMainActivity();
            }
        });

        likesLoader.loadAllLikes();
    }

    private void startMainActivity() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
