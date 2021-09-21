package com.example.harry.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.harry.myapplication.webview.MuduRoom
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        entry_button.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.entry_button -> {
                val url = url_text?.text.toString()
                startActivity(Intent(this, MuduRoom::class.java).putExtra(URL_MESSAGE, url))
            }
        }
    }

    companion object {
        const val URL_MESSAGE = "com.example.harry.myapplication.URL_MESSAGE"
    }
}