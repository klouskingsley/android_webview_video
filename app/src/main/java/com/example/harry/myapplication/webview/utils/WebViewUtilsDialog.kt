package com.example.harry.myapplication.webview.utils

import android.app.AlertDialog
import com.example.harry.myapplication.webview.MuduRoom
import com.example.harry.myapplication.webview.url2bitmap
import kotlinx.android.synthetic.main.activity_mudu_room.*

fun MuduRoom.dialogSaveImageToLocalStorage(){
    // 弹出保存图片的对话框
    val builder = AlertDialog.Builder(this)
    builder.setTitle("提示")
    builder.setMessage("保存图片到本地")
    builder.setPositiveButton(
        "确认"
    ) { _, _ ->
        val picUrl = myWebView.hitTestResult.extra //获取图片链接
        //保存图片到相册
        Thread { url2bitmap(picUrl) }.start()
    }
    builder.setNegativeButton(
        "取消"
    ) // 自动dismiss
    { _, _ -> }
    val dialog = builder.create()
    dialog.show()
}