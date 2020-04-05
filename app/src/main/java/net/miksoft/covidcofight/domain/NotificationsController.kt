package net.miksoft.covidcofight.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import net.miksoft.covidcofight.MainActivity
import net.miksoft.covidcofight.R


object NotificationsController {

    private lateinit var applicationContext: Context

    private const val CHANNEL_ID = "default"
    private const val REQUEST_CODE = 321
    private const val NOTIFICATION_ID = 1

    private val notificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val pendingIntent by lazy {
        val intent = Intent(
            applicationContext,
            MainActivity::class.java
        )
        PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            intent,
            FLAG_UPDATE_CURRENT
        )
    }

    fun init(applicationContext: Context) {
        this.applicationContext = applicationContext
    }

    fun setRunningNotification(enabled: Boolean) {
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, "Default", NotificationManager.IMPORTANCE_LOW)
                )
            }

            val notification =
                NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setContentText(applicationContext.getString(R.string.notification_running))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.launcher_icon)
                    .setOngoing(true)
                    .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

}