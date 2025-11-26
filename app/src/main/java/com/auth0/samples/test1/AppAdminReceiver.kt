package com.auth0.samples.test1

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AppAdminReceiver : DeviceAdminReceiver(){
    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        Toast.makeText(context, "Get out of Kiosk Mode!", Toast.LENGTH_SHORT).show()
        return super.onLockTaskModeExiting(context, intent)
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(context, "Device admin enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, "Device admin disabled", Toast.LENGTH_SHORT).show()
    }
}