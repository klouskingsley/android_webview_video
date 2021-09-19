package com.example.harry.myapplication.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.OnLongClickListener
import android.webkit.*
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebView.HitTestResult
import androidx.appcompat.app.AppCompatActivity
import com.example.harry.myapplication.MainActivity
import com.example.harry.myapplication.R
import com.example.harry.myapplication.webview.utils.dialogSaveImageToLocalStorage
import kotlinx.android.synthetic.main.activity_mudu_room.*
import java.io.*

class MuduRoom : AppCompatActivity() {

    // webview上传文件临时变量存储
    internal var filePathCallback: ValueCallback<Array<Uri>>? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mudu_room)

        // 获取传过来的html页面地址
        //val intent = intent
        val url = intent.getStringExtra(MainActivity.URL_MESSAGE)
        myWebView.setOnLongClickListener(OnLongClickListener {
            val hitTestResult = myWebView.hitTestResult
            // 如果是图片类型或者是带有图片链接的类型
            if (hitTestResult.type == HitTestResult.IMAGE_TYPE ||
                hitTestResult.type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE)
            {
                dialogSaveImageToLocalStorage()
                return@OnLongClickListener true
            }
            false //保持长按可以复制文字
        })
        myWebView.webChromeClient = MyWebChromeClient(this@MuduRoom)

        // 设置WebViewClient，防止使用浏览器打开
        myWebView.webViewClient = WebViewClient()
        // 加载html页面地址
        myWebView.loadUrl(url?: "")

        // 设置允许javascript及domStorage
        val webSettings = myWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 接收文件上传消息
        if (requestCode == FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5) {
            if (filePathCallback == null) return
            filePathCallback!!.onReceiveValue(FileChooserParams.parseResult(resultCode, data))
            filePathCallback = null
        }
    }

    companion object {
        const val FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5 = 5174
    }
}
