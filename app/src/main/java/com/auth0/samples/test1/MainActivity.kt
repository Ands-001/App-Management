package com.auth0.samples.test1

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dpm: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = ComponentName(this, AppAdminReceiver::class.java)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        startActivity(Intent(this, WorkspaceActivity::class.java))
        finish()
    }

    override fun onResume(){
        super.onResume()

        if(!dpm.isLockTaskPermitted(packageName)){
            return
        }
        startKiskMode()
    }

    private fun startKiskMode(){
        dpm.setStatusBarDisabled(adminComponentName, true)

        if (dpm.isDeviceOwnerApp(packageName)){
            dpm.setLockTaskPackages(adminComponentName, arrayOf(packageName))
        }

        startLockTask()
    }

//    private fun outPutKiskMode(){
//        stopLockTask()
//        dpm.setStatusBarDisabled(adminComponentName, false)
//    }
}