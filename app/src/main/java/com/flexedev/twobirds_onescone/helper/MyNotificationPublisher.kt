package com.flexedev.twobirds_onescone.helper

import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.flexedev.twobirds_onescone.R

/**
 * https://www.tutorialspoint.com/how-to-create-android-notification-with-broadcastreceiver
 */
class MyNotificationPublisher : BroadcastReceiver() {

    private var NOTIFICATION_ID = 0
    private var NOTIFICATION_CHANNEL_ID = "2Birds1Scone_Channel"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefs: SharedPreferences

    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferences = context!!.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        saveData()
        showNotification(context, NOTIFICATION_ID)

        NotificationHelper().scheduleNotification(context, sharedPreferences)
    }

    private fun saveData() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("lastSconeReviewedTime", System.currentTimeMillis().toString())
        }.apply()
    }

    private fun showNotification(context: Context, notificationId: Int) {
        val interval = prefs.getString("alertIntervals", "awhile")

        // Open the app when the notification is clicked
        val resultIntent = Intent(context, com.flexedev.twobirds_onescone.MainActivity::class.java)
        val resultIntentPending: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.star_icon)
            .setContentTitle("Get your scone fix")
            .setContentText("You haven't reviewed a scone in $interval!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(resultIntentPending)


        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

}