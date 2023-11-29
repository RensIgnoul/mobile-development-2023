package com.example.padelappproject

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class DayDetailActivity:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_detail)

        val testString = intent.getStringExtra("EXTRA_STRING")
        val textView = findViewById<TextView>(R.id.buttonTest)
        textView.text = testString
    }
}