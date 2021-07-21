package com.flexedev.twobirds_onescone.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class NotificationHelper {
    private val _24Hours: String = "24 Hours"

    /**
     * :))))))))))))
     */
    private fun getDelayFromInterval(interval: String?): Long {
        val timeInSec: Long = 360000
        when (interval) {

            "30 seconds" -> {
                return 30 * 1000
            }

            _24Hours -> {
                return timeInSec * 24
            }

            "2 days" -> {
                return timeInSec * 48
            }

            "3 days" -> {
                return timeInSec * 72
            }

            "5 days" -> {
                return timeInSec * 120
            }

            "1 week" -> {
                return timeInSec * 168
            }

            "2 weeks" -> {
                return timeInSec * 336
            }
        }
        return timeInSec * 24
    }

    fun updateNotificationsEnabled(
        value: Any,
        context: Context,
        sharedPreferences: SharedPreferences
    ) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val interval = prefs.getString("alertIntervals", _24Hours)

        if (!(value as Boolean)) {
            try {
                val intent = Intent(context, MyNotificationPublisher::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val alarmManager =
                    (context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager)

                val pendingIntent =
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )
                alarmManager.cancel(pendingIntent)
            } catch (error: Exception) {
                Log.d("ERROR", error.toString())
            }
        }
        scheduleNotification_(context, sharedPreferences, value, interval)
    }

    fun updateNotificationsInterval(
        value: Any,
        context: Context,
        sharedPreferences: SharedPreferences
    ) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val receiveAlerts = prefs.getBoolean("enableAlerts", false)
        scheduleNotification_(context, sharedPreferences, receiveAlerts, value.toString())
    }

    fun scheduleNotification(context: Context, sharedPreferences: SharedPreferences) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val receiveAlerts = prefs.getBoolean("enableAlerts", false)
        val interval = prefs.getString("alertIntervals", _24Hours)

        scheduleNotification_(context, sharedPreferences, receiveAlerts, interval)
    }


    private fun scheduleNotification_(
        context: Context,
        sharedPreferences: SharedPreferences,
        receiveAlerts: Boolean,
        interval: String?
    ) {
        val lastSconeReviewedString: String? =
            sharedPreferences.getString("lastSconeReviewedTime", null)

        if (lastSconeReviewedString == null) {
            // If we haven't reviewed a scone yet, just make the "last reviewed time" now lol
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.apply {
                putString("lastSconeReviewedTime", SystemClock.elapsedRealtime().toString())
            }.apply()
        }

        if (receiveAlerts && lastSconeReviewedString != null) {

            val lastSconeReviewed = lastSconeReviewedString.toLong()

            val intent = Intent(context, MyNotificationPublisher::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val alarmManager =
                (context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager)

            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val futureInMillis =
                lastSconeReviewed + NotificationHelper().getDelayFromInterval(interval)

            alarmManager.set(
                AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent
            )
        }
    }
}