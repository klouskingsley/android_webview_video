package com.example.harry.myapplication;

import android.content.pm.ActivityInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ConstraintLayout customViewContainer = findViewById(R.id.layout);
        final WebView myWebView = (WebView) findViewById(R.id.webview);
        final AppCompatActivity curActivity = this;

        myWebView.setWebChromeClient(new WebChromeClient(){

            private CustomViewCallback exitFulscreenFunc;
            private View customView;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                Log.d(TAG, "onShowCustomView: ");
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                ));
                ((MainActivity) curActivity).hideTitleBar();
                myWebView.setVisibility(View.GONE);
                customViewContainer.setVisibility(View.VISIBLE);
                customViewContainer.addView(view);
                exitFulscreenFunc = callback;
                customView = view;
                curActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                Log.d(TAG, "onHideCustomView: ");
                ((MainActivity) curActivity).showTitleBar();
                myWebView.setVisibility(View.VISIBLE);
                customViewContainer.removeView(customView);
                exitFulscreenFunc.onCustomViewHidden();
                curActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("http://mudu.tv/?c=activity&a=live&id=156450");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
    }

    public void hideTitleBar () {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // hide status bar and nav bar after a short delay, or if the user interacts with the middle of the screen
        );
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void showTitleBar () {
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
