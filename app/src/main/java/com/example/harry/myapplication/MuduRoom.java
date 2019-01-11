package com.example.harry.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MuduRoom extends AppCompatActivity {

    private final String TAG = "MuduRoom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mudu_room);

        final ConstraintLayout customViewContainer = findViewById(R.id.layout);
        final WebView myWebView = (WebView) findViewById(R.id.webview);
        final AppCompatActivity curActivity = this;

        // 获取传过来的html页面地址
        Intent intent = getIntent();
        String url = intent.getStringExtra(MainActivity.URL_MESSAGE);

        myWebView.setWebChromeClient(new WebChromeClient(){

            private CustomViewCallback exitFulscreenFunc;
            private View customView;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                Log.d(TAG, "onShowCustomView: ");

                // 全屏view是framelayout，需要设置它的layoutParams为match_parent，否则有可能全屏后下面出现空白
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                ));

                // 隐藏title Bar及status bar
                ((MuduRoom) curActivity).hideTitleBar();

                // 将原来的webView隐藏
                myWebView.setVisibility(View.GONE);

                // 将全屏view设为可见，并添加到页面中
                customViewContainer.setVisibility(View.VISIBLE);
                customViewContainer.addView(view);

                // 保存退出全屏的方法
                exitFulscreenFunc = callback;

                // 保存全屏view
                customView = view;

                // 设置屏幕为横屏
                curActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                Log.d(TAG, "onHideCustomView: ");

                // 显示title bar及status bar
                ((MuduRoom) curActivity).showTitleBar();

                // 将原来的webview设为可见
                myWebView.setVisibility(View.VISIBLE);

                // 移除全屏view
                customViewContainer.removeView(customView);

                // 调用退出全屏的方法
                exitFulscreenFunc.onCustomViewHidden();

                // 设置屏幕为竖屏
                curActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        // 设置WebViewClient，防止使用浏览器打开
        myWebView.setWebViewClient(new WebViewClient());
        // 加载html页面地址
        myWebView.loadUrl(url);

        // 设置允许javascript及domStorage
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
    }

    public void hideTitleBar () {

        // 设置全屏及status bar自动隐藏
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // hide status bar and nav bar after a short delay, or if the user interacts with the middle of the screen
        );

        // 隐藏actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 设置屏幕常亮，全屏后过一段时间屏幕可能会变暗
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void showTitleBar () {
        // 显示status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // 显示actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        // 取消屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
