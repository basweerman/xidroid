package xi.xidroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class XiDroidBrowserScreen extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xi_droid_browser_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("XiDroid Browser");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        String url = b.getString("page");


        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new webViewClient());
        webView.setWebChromeClient(new chromeWebViewClient());

        //webView.Settings.PluginsEnabled = true;

        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        //webSettings.setp
        //webView.clearCache(true);
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);

    }


    private class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }


    private class chromeWebViewClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
        {
            return true;
        }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                // Show a grant or deny dialog to the user
String test = "as";

                test = test + "asasdsa";
                // On accept or deny call
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
                // or
                // request.deny()
            }

    }


}
