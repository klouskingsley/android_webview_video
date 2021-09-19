package com.example.harry.myapplication.webview

import android.view.View
import android.view.WindowManager

fun MuduRoom.showTitleBar() {
    // 显示status bar
    val decorView = window.decorView
    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

    // 显示actionBar
    val actionBar = supportActionBar
    actionBar!!.show()

    // 取消屏幕常亮
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun MuduRoom.hideTitleBar() {

    // 设置全屏及status bar自动隐藏
    val decorView = window.decorView
    decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // hide status bar and nav bar after a short delay, or if the user interacts with the middle of the screen
            )

    // 隐藏actionBar
    val actionBar = supportActionBar
    actionBar!!.hide()

    // 设置屏幕常亮，全屏后过一段时间屏幕可能会变暗
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}