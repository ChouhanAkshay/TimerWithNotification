package com.example.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.example.timer.services.TimerService

class MainActivity : AppCompatActivity() {
    lateinit var receiver : BroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListeners()

        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.timer")

        receiver = object : BroadcastReceiver() {
            val textView = findViewById<AppCompatTextView>(R.id.tv_timer)

            override fun onReceive(context: Context?, intent: Intent?) {
                textView.text = intent?.getStringExtra(TimerService.TIME)
            }
        }

        registerReceiver(receiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners() {
        findViewById<Button>(R.id.btn_start_stop).setOnClickListener {
            val intent = Intent(this, TimerService::class.java).also {
                it.action = TimerService.START
            }
            startForegroundService(intent)
        }
    }
}