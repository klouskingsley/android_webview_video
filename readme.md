# 安卓webview视频播放全屏处理示例


## AndroidManifest设置

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

## activity处理

对activity主要进行了一下处理

- 1. 设置webViewChromeClient
- 2. 实现webViewChromeClient.onShowCustomView方法，该方法是webview全屏时被调用的方法, 方法具体内容参考代码
- 3. 实现webViewChromeClient.onShowCustomView方法，该方法是webview退出全屏时被调用的方法，方法具体内容参考代码
- 4. 设置webViewClient防止使用浏览器打开