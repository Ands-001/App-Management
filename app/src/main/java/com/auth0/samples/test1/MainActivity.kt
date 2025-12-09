package com.auth0.samples.test1

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

        if(dpm.isDeviceOwnerApp(packageName)){
            val filter = IntentFilter(Intent.ACTION_MAIN).apply{
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
             dpm.addPersistentPreferredActivity(adminComponentName, filter, ComponentName(packageName, MainActivity::class.java.name))
        }

        startActivity(Intent(this, WorkspaceActivity::class.java))
        finish()
    }

    override fun onResume(){
        super.onResume()

        try {
            if(dpm.isLockTaskPermitted(packageName)){
                startKiskMode()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun startKiskMode(){
        if (dpm.isDeviceOwnerApp(packageName)){
            dpm.setStatusBarDisabled(adminComponentName, true)
            dpm.setLockTaskPackages(adminComponentName, arrayOf(packageName))
        }
        
        startLockTask()
    }

//    private fun outPutKiskMode(){
//        stopLockTask()
//        dpm.setStatusBarDisabled(adminComponentName, false)
//    }
}