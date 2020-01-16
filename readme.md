## 1.安卓webview视频播放全屏处理示例


#### AndroidManifest设置

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.harry.myapplication">

    <!--需要允许网络-->
    <uses-permission android:name="android.permission.INTERNET" />

    <!--android9开始usesCleartextTraffic默认为false, 需要设置true-->
    <application
        android:usesCleartextTraffic="true"
        android:allowBackup="true"
        android:hardwareAccelerated="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity
            android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!--webview所在activity需要设置configChanges-->
        <activity
            android:name=".MuduRoom"
            android:configChanges="orientation|screenSize|keyboardHidden">
        </activity>
    </application>

</manifest>
```

#### activity处理

对activity主要进行了一下处理

- 1. 设置webViewChromeClient
- 2. 实现webViewChromeClient.onShowCustomView方法，该方法是webview全屏时被调用的方法, 方法具体内容参考代码
- 3. 实现webViewChromeClient.onShowCustomView方法，该方法是webview退出全屏时被调用的方法，方法具体内容参考代码
- 4. 设置webViewClient防止使用浏览器打开

> 注：statusBar的处理可能在兼容性方面有些问题，需要自己调整下

## 2. 倍速问题

-  使用基于x5内核或者chrome内核的webview
-  安卓自带webview播放器默认使用hlsjs
 ```
 前端处理
 provider: ['hlsjs']
 ```
 
## 3. 文档下载/复制链接无效

 原因
 ```
 FileSaver.saveAs(docUrl, filename + '.' + ext) 在webview中不支持
 ```
 
现在方案是移动端统一复制链接, 业务代码修复
```
DownloadBtn组件判断许覆盖Android
if (isWeixin || Browser.isiOS || Browser.isAndroid) {
} else {
}
```

## 4. 长按二维码下载
长按识别需要在客户端完成下载, 示例代码
```
添加权限 
AndroidManifest.xml

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.SET_WALLPAPER"/>
```
```
MuduRoom.xml

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
                            // demo就直接写在这里了, 最好放到线程池中统一管理
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
```

```
 //    @Override
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
```

## 5. 话题互动 添加上传图片

客户端增加文档读写权限
上传图片时需要动态获取权限

```
添加权限 
AndroidManifest.xml

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.SET_WALLPAPER"/>

动态调用/使用

```


## demo
[apk](https://github.com/klouskingsley/android_webview_video/releases/download/1.2/app-debug.apk)
