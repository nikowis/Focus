package pl.nikowis.focus.ui.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 5/18/2017.
 */

public class PinActivity extends AppCompatActivity {
    public static final String TAG = "PinLockView";

    @BindView(R.id.pin_lock_view)
    PinLockView mPinLockView;

    @BindView(R.id.indicator_dots)
    IndicatorDots mIndicatorDots;

    @BindView(R.id.pin_text_view)
    TextView pinTextView;

    private String newPin = null;

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String currentPin = prefs.getString(getApplicationContext().getString(R.string.key_pref_pin), null);
            if (currentPin == null && newPin != null) {
                if (pin.equals(newPin)) {
                    prefs.edit().putString(getApplicationContext().getString(R.string.key_pref_pin), pin).apply();
                    startMainActivity();
                } else {
                    newPin = null;
                    pinTextView.setText(R.string.pin_do_not_match);
                    mPinLockView.resetPinLockView();
                }
            } else if (pin.equals(currentPin)) {
                startMainActivity();
            } else if (currentPin == null) {
                newPin = pin;
                pinTextView.setText(R.string.pin_repeat);
                mPinLockView.resetPinLockView();
            } else {
                pinTextView.setText(R.string.pin_incorrect);
                mPinLockView.resetPinLockView();
            }
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pin);

        ButterKnife.bind(this);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);

        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String currentPin = prefs.getString(getApplicationContext().getString(R.string.key_pref_pin), null);

        if(currentPin == null) {
            pinTextView.setText(R.string.pin_enter_new);
        } else {
            pinTextView.setText(R.string.pin_enter);
        }

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
    }

    private void startMainActivity() {
        Intent i = new Intent(PinActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
