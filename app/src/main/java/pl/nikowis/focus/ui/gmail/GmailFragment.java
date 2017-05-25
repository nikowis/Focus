package pl.nikowis.focus.ui.gmail;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.gmail.GmailScopes;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.base.SettingsActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class GmailFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.gmail_login_button)
    Button loginButton;
    @BindView(R.id.gmail_post_list)
    RecyclerView recyclerView;
    @BindView(R.id.gmail_fab_load_more)
    FloatingActionButton loadMorePostsButton;

    private GoogleAccountCredential mCredential;
    private static Context context;
    private Unbinder unbinder;
    private GmailPostsAdapter gmailAdapter;
    private boolean userLoggedIn;
    private List<GmailMessage> gmailMessages;
    private SharedPreferences prefs;
    private GmailFeedLoader gmailFeedLoader;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_KEY_GMAIL_ACCOUNT_NAME = "pref_gmail_accountName";
    private static final String[] SCOPES = {GmailScopes.MAIL_GOOGLE_COM};

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainFragment = inflater.inflate(R.layout.fragment_gmail, container, false);
        unbinder = ButterKnife.bind(this, mainFragment);
        context = getContext();

        gmailAdapter = new GmailPostsAdapter(getContext(), new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                startActivity(intent);

                return true;
            }
        });
        gmailMessages = gmailAdapter.getList();

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        userLoggedIn = prefs.getBoolean(context.getString(R.string.key_pref_gmail_logged_in), false);
        String accountName = prefs.getString(context.getString(R.string.key_pref_gmail_account_name), null);
        mCredential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        loadMorePostsButton.setVisibility(View.GONE);
        if (userLoggedIn && accountName != null) {
            loginButton.setVisibility(View.GONE);
            mCredential.setSelectedAccountName(accountName);
            resetGmailFeedLoader();
        }
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(context.getString(R.string.key_pref_gmail_logout))) {
                    loginButton.setVisibility(View.VISIBLE);
                    loadMorePostsButton.setVisibility(View.GONE);
                    resetGmailFeedLoader();
                } else if (key.equals(context.getString(R.string.key_pref_gmail_page_count))) {
                    resetGmailFeedLoader();
                }
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(gmailAdapter);
        return mainFragment;
    }

    @OnClick(R.id.gmail_login_button)
    public void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(context, R.string.no_network_available, Toast.LENGTH_SHORT).show();
        } else {
            authorizationSuccessfutl();
        }
    }

    @OnClick(R.id.gmail_fab_load_more)
    public void loadContent() {
        gmailFeedLoader.loadContent();
    }

    private void authorizationSuccessfutl() {
        userLoggedIn = true;
        loginButton.setVisibility(View.GONE);
        resetGmailFeedLoader();
        prefs.edit().putBoolean(context.getString(R.string.key_pref_gmail_logged_in), true).apply();
        prefs.edit().putString(context.getString(R.string.key_pref_gmail_account_name), mCredential.getSelectedAccountName()).apply();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                context, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = this.getActivity().getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_KEY_GMAIL_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER
                );
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.gmail_access_rationale),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS
            );
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(context, R.string.install_google_play_services, Toast.LENGTH_SHORT).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = this.getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_KEY_GMAIL_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private void resetGmailFeedLoader() {
        gmailFeedLoader = new GmailFeedLoader(context, mCredential, gmailAdapter, new GmailFeedLoader.ContentLoaderEventsListener() {
            @Override
            public void readyToDisplay() {
                Toast.makeText(context, R.string.loader_ready, Toast.LENGTH_SHORT).show();
                if (userLoggedIn && loadMorePostsButton != null) {
                    loadMorePostsButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void loadingMoreData() {
                Toast.makeText(getActivity(), R.string.loader_loading, Toast.LENGTH_SHORT).show();
                if (loadMorePostsButton != null) {
                    loadMorePostsButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
