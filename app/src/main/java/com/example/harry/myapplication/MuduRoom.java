package com.example.harry.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;
import android.widget.Toast;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.os.Build;

public class MuduRoom extends AppCompatActivity {

    private final String TAG = "MuduRoom";

    // webview上传文件临时变量存储
    public ValueCallback<Uri[]> filePathCallback;
    public static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 5174;


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

        myWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final WebView.HitTestResult hitTestResult = myWebView.getHitTestResult();
                // 如果是图片类型或者是带有图片链接的类型
                if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    // 弹出保存图片的对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(curActivity);
                    builder.setTitle("提示");
                    builder.setMessage("保存图片到本地");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final String picUrl = hitTestResult.getExtra();//获取图片链接
                            //保存图片到相册
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    url2bitmap(picUrl);
                                }
                            }).start();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        // 自动dismiss
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                return false;//保持长按可以复制文字
            }
        });

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

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // android5.0+ webview上传文件实现， 参考：https://juejin.im/post/585a4d0b128fe1006b906f16
                if (((MuduRoom) curActivity).filePathCallback != null) {
                    ((MuduRoom) curActivity).filePathCallback.onReceiveValue(null);
                    ((MuduRoom) curActivity).filePathCallback = null;
                }
                ((MuduRoom) curActivity).filePathCallback = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    ((MuduRoom) curActivity).startActivityForResult(intent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
                } catch (ActivityNotFoundException e) {
                    ((MuduRoom) curActivity).filePathCallback = null;
                    return false;
                }
                return true;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 接收文件上传消息
        if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (filePathCallback == null) return;
            filePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            filePathCallback = null;
        }
    }

    public void url2bitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
            if (bm != null) {
                save2Album(bm, url);
            }
            System.out.println("===>muduroom6done");
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }

    private void save2Album(Bitmap bitmap, String picUrl) {
        //       需要另外获取权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MuduRoom.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MuduRoom.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MuduRoom.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MuduRoom.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MuduRoom.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MuduRoom.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.READ_SMS}, 101);
            }
        }
        File appDir = new File(Environment.getExternalStorageDirectory(), "/");
        if (!appDir.exists()) appDir.mkdir();
        String[] str = picUrl.split("/");
        String fileName = str[str.length - 1];
        File file = new File(appDir, fileName);
        try {
            System.out.println("===>muduroom1!");
            System.out.println(file);
            FileOutputStream fos = new FileOutputStream(file);
            System.out.println("===>muduroom!");
            System.out.println(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            onSaveSuccess(file);
        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }

    private void onSaveSuccess(final File file) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
