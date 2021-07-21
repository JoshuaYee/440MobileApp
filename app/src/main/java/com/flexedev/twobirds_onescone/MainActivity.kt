package com.flexedev.twobirds_onescone

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.flexedev.twobirds_onescone.fragments.settings.SettingsActivity
import com.flexedev.twobirds_onescone.helper.PermissionsRequest


class MainActivity : AppCompatActivity() {

    private val hasLocationPermissions
        get() = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private val CHANNEL_ID = "2Birds1Scone_Channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.flexedev.twobirds_onescone.R.layout.activity_main)

        createNotificationChannel()

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
        requestPermissions(permissions, 100, {
            promptForGPS()
            if (hasLocationPermissions) {
                Log.d("TRUE", "TRUE")
            } else {
                Log.d("FALSE", "FALSE")
            }
        }, {
            Toast.makeText(
                this,
                "GPS not permitted. You will not be able to unlock hiddenMessage messages.",
                Toast.LENGTH_LONG
            ).show()
        })
    }

    private fun createNotificationChannel() {
        Log.d("NOTIFICATION", "Trying to create notification channel")
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("NOTIFICATION", "API 26+ yes")
            val name = "2Birds1Scone"
            val descriptionText = "Periodic scone-minders to review scones."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } else {
            Log.d("NOTIFICATION", "API < 26 :(")
        }
    }

    private fun promptForGPS() {
        if (hasLocationPermissions) {
            Log.d("TRUE", "TRUE")
        } else {
            Log.d("FALSE", "FALSE")
        }
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(this).apply {
                setMessage("GPS is not enabled on your device. Enable it in the location settings.")
                setPositiveButton("Settings") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                setNegativeButton("Cancel") { _, _ -> }
                show()
            }
        }
    }

    private val requests: MutableMap<Int, PermissionsRequest> = mutableMapOf()
    private fun requestPermissions(
        permissions: Array<String>,
        requestId: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        // The WRITE_SETTINGS permission must be granted using a different
        // scheme. Frustrating.
        val hasWriteSettings = permissions.contains(Manifest.permission.WRITE_SETTINGS)
        val needsWriteSettings = hasWriteSettings && !Settings.System.canWrite(this)
        val remaining = if (hasWriteSettings) {
            permissions.filter { it != Manifest.permission.WRITE_SETTINGS }
        } else {
            permissions.toList()
        }

        // If we're on early Android, runtime requests are not needed,
        // so we assume permission has already been granted by listing
        // the permissions in the manifest.
        val ungranted = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> listOf()
            else -> remaining.filter {
                ActivityCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }
        }

        // If all but the WRITE_SETTINGS permission has been granted...
        if (ungranted.isEmpty()) {
            if (needsWriteSettings) {
                requests[requestId] = PermissionsRequest(needsWriteSettings, onSuccess, onFailure)
                promptForWriteSettings(requestId)
            } else {
                onSuccess()
            }
        }

        // Otherwise, request the ungranted permissions.
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requests[requestId] = PermissionsRequest(needsWriteSettings, onSuccess, onFailure)
            ActivityCompat.requestPermissions(this, ungranted.toTypedArray(), requestId)
        }
    }

    private fun promptForWriteSettings(requestId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("This operation requires the ability to modify system settings. Please grant this permission on the next screen.")
        builder.setPositiveButton("Okay") { _, _ ->
            startActivityForResult(
                Intent(
                    Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:$packageName")
                ), requestId
            )
        }
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.flexedev.twobirds_onescone.R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.flexedev.twobirds_onescone.R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
