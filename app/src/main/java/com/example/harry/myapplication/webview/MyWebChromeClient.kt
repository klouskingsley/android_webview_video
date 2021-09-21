package com.example.harry.myapplication.webview

import android.content.ActivityNotFoundException
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_mudu_room.*

class MyWebChromeClient(val activity: AppCompatActivity) : WebChromeClient() {

    private val TAG = "MuduRoom_WebChromeClient"

    private var exitFulscreenFunc: CustomViewCallback? = null
    private var customView: View? = null
    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        super.onShowCustomView(view, callback)
        Log.d(TAG, "onShowCustomView: ")

        // 全屏view是framelayout，需要设置它的layoutParams为match_parent，否则有可能全屏后下面出现空白
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        // 隐藏title Bar及status bar
        (activity as MuduRoom).hideTitleBar()

        // 将原来的webView隐藏
        activity.myWebView.visibility = View.GONE

        // 将全屏view设为可见，并添加到页面中
        activity.customViewContainer.visibility = View.VISIBLE
        activity.customViewContainer.addView(view)

        // 保存退出全屏的方法
        exitFulscreenFunc = callback

        // 保存全屏view
        customView = view

        // 设置屏幕为横屏
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        Log.d(TAG, "onHideCustomView: ")

        // 显示title bar及status bar
        (activity as MuduRoom).showTitleBar()

        // 将原来的webview设为可见
        activity.myWebView.visibility = View.VISIBLE

        // 移除全屏view
        activity.customViewContainer.removeView(customView)

        // 调用退出全屏的方法
        exitFulscreenFunc!!.onCustomViewHidden()

        // 设置屏幕为竖屏
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        // android5.0+ webview上传文件实现， 参考：https://juejin.im/post/585a4d0b128fe1006b906f16
        if ((activity as MuduRoom).filePathCallback != null) {
            activity.filePathCallback!!.onReceiveValue(null)
            activity.filePathCallback = null
        }

        activity.filePathCallback = filePathCallback
        val intent = fileChooserParams.createIntent()
        try {
            activity.startActivityForResult(
                intent,
                MuduRoom.FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5
            )
        } catch (e: ActivityNotFoundException) {
            activity.filePathCallback = null
            return false
        }
        return true
    }
}