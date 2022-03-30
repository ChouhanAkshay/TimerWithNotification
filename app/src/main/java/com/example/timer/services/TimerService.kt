package com.example.timer.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.timer.MainActivity
import com.example.timer.R

class TimerService : Service() {

    private lateinit var notificationBuilder : NotificationCompat.Builder
    private lateinit var notificationManger : NotificationManager

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_ONE_SHOT)

        val notificationChannel =
            NotificationChannel("0", "timer", NotificationManager.IMPORTANCE_DEFAULT)

        notificationManger =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManger.createNotificationChannel(notificationChannel)

        notificationBuilder = NotificationCompat.Builder(this).setContentTitle("timer")
            .setContentText("00:00")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setChannelId("0")

        //start countdown
//        startForeground(1, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, notificationBuilder.build())

        when (intent?.action) {
            START -> {
                object : CountDownTimer(50000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        //todo convert mills to timer
                        sendBroadcastValue(millisUntilFinished.toString())

                        val notification =
                            notificationBuilder.setContentText(millisUntilFinished.toString())
                                .build()
                        notificationManger.notify(1, notification)
                    }

                    override fun onFinish() {
                        sendBroadcastValue("0")
                        val notification = notificationBuilder.setContentText("0").build()
                        notificationManger.notify(1, notification)
                        Toast.makeText(this@TimerService, "finished", Toast.LENGTH_LONG).show()
                    }
                }.start()
            }
            STOP -> {
                stopForeground(true)
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun sendBroadcastValue(seconds: String) {
        val intent = Intent()
        intent.putExtra(TIME, seconds)
        intent.action = "com.example.timer"
        sendBroadcast(intent)
    }

    companion object {
        const val START = "start"
        const val STOP = "stop"
        const val TIME = "time"
    }
}