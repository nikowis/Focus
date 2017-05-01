package pl.nikowis.focus.ui.instagram;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.nikowis.focus.R;
import pl.nikowis.focus.rest.instagram.InstagramRequestManager;

/**
 * Created by Nikodem on 4/30/2017.
 */

public class InstagramLoginDialog extends Dialog {
    private String mUrl;
    private OAuthDialogListener mListener;
    @BindView(R.id.instagram_webview)
    WebView mWebView;

    public InstagramLoginDialog(Context context, String url, OAuthDialogListener listener) {
        super(context);
        mUrl = url;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_instagram);
        ButterKnife.bind(this);
        mWebView.setWebViewClient(new OAuthWebViewClient());
        mWebView.loadUrl(mUrl);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
    }

    private class OAuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("Instagram-WebView", "Redirecting URL " + url);
            if (url.startsWith(InstagramRequestManager.REDIRECT_URI)) {
                String urls[] = url.split("=");
                mListener.onComplete(urls[1]);
                InstagramLoginDialog.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.d("Instagram-WebView", "Page error: " + error.toString());
            super.onReceivedError(view, request, error);
            mListener.onError(error.toString());
            InstagramLoginDialog.this.dismiss();
        }
    }

    public interface OAuthDialogListener {
        public abstract void onComplete(String accessToken);

        public abstract void onError(String error);
    }
}
