package com.example.starhoshino

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tv = TextView(this)
        tv.text = "星野已启动 ✅\nWebView 还没接回来"
        tv.textSize = 22f
        tv.setPadding(40, 80, 40, 40)
        setContentView(tv)
    }
}
