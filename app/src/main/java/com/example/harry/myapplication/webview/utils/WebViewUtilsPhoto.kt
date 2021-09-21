package com.example.harry.myapplication.webview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

fun MuduRoom.url2bitmap(url: String?) {
    var bm: Bitmap? = null
    try {
        val iconUrl = URL(url)
        val conn = iconUrl.openConnection()
        val http = conn as HttpURLConnection
        val length = http.contentLength
        conn.connect()
        // 获得图像的字符流
        val `is` = conn.getInputStream()
        val bis = BufferedInputStream(`is`, length)
        bm = BitmapFactory.decodeStream(bis)
        bis.close()
        `is`.close()
        bm?.let { save2Album(it, url) }
        println("===>muduroom6done")
    } catch (e: Exception) {
        runOnUiThread { Toast.makeText(applicationContext, "保存失败", Toast.LENGTH_SHORT).show() }
        e.printStackTrace()
    }
}

fun MuduRoom.save2Album(bitmap: Bitmap, picUrl: String?) {
    //       需要另外获取权限
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_SMS
                ), 101
            )
        }
    }
    val appDir = File(Environment.getExternalStorageDirectory(), "/")
    if (!appDir.exists()) appDir.mkdir()
    val str = picUrl!!.split("/").toTypedArray()
    val fileName = str[str.size - 1]
    val file = File(appDir, fileName)
    try {
        println("===>muduroom1!")
        println(file)
        val fos = FileOutputStream(file)
        println("===>muduroom!")
        println(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        onSaveSuccess(file)
    } catch (e: IOException) {
        runOnUiThread { Toast.makeText(applicationContext, "保存失败", Toast.LENGTH_SHORT).show() }
        e.printStackTrace()
    }
}

fun MuduRoom.onSaveSuccess(file: File) {
    runOnUiThread {
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
        Toast.makeText(applicationContext, "保存成功", Toast.LENGTH_SHORT).show()
    }
}