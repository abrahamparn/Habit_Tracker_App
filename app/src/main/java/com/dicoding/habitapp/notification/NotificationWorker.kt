package com.dicoding.habitapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.add.AddHabitActivity
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
    private val habitTitle = inputData.getString(HABIT_TITLE)

    override fun doWork(): Result {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify = prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        //TODO 12 : If notification preference on, show notification with pending intent

        if(shouldNotify){

            val notificationStyle = NotificationCompat.InboxStyle()

            val intent = Intent(applicationContext, AddHabitActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)

            val mNotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val textContent = applicationContext.getString(R.string.notify_content)
            val mBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(habitTitle)
                .setContentText(textContent)
                .setStyle(notificationStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)


            /*
      Untuk android Oreo ke atas perlu menambahkan notification channel
      */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                /* Create or update. */
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIF_UNIQUE_WORK, NotificationManager.IMPORTANCE_DEFAULT)

                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)

                mNotificationManager.createNotificationChannel(channel)
            }

            val notification = mBuilder.build()

            mNotificationManager.notify(habitId, notification)
        }

        return Result.success()
    }

}
