package com.auth0.samples.test1


import android.app.KeyguardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context

class WorkspaceActivity : AppCompatActivity() {
    private lateinit var dpm: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace)

        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = ComponentName(this, AppAdminReceiver::class.java)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.elevation = 0f
        loadApps()
        FixScreen()
    }

    private fun loadApps() {
        val selectedApps = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getStringSet("selected_apps", mutableSetOf()) ?: mutableSetOf()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 4)

        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { selectedApps.contains(it.packageName) }

        // Libera os apps selecionados
        if (dpm.isDeviceOwnerApp(packageName)) {
            val allowedApps = selectedApps.toMutableSet()
            allowedApps.add(packageName) // Importante: Manter o próprio app na lista
            dpm.setLockTaskPackages(adminComponentName, allowedApps.toTypedArray())
        }
        
        val adapter = AppAdapter(
            apps = apps,
            selectedApps = mutableSetOf(),
            onAppClick = { app, _ ->
                val intent = pm.getLaunchIntentForPackage(app.packageName)
                if (intent != null) {
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            },
            useGridLayout = true
        )
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.workspace_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_admin_login -> {
                showAdminPasswordDialog(item)
                true
            }

            R.id.action_admin_logout -> {
                showAdminPasswordDialog(item)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun showAdminPasswordDialog(item: MenuItem) {
        val editText = EditText(this)
        editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        editText.setTextColor(android.graphics.Color.BLACK)
        val padding = (16 * resources.displayMetrics.density).toInt()
        editText.setPadding(padding, padding, padding, padding)

        val builder = AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)


        builder.setTitle("Acesso Administrativo")
            .setMessage("Digite a senha")
            .setView(editText)
            .setPositiveButton("Entrar") { dialog, _ ->
                val password = editText.text.toString()

                if (password == "1234") {
                    if (item.itemId == R.id.action_admin_login){
                        startActivity(Intent(this, AppSelectionActivity::class.java))
                    } else if (item.itemId == R.id.action_admin_logout){
                        outScreen()
                    }
                } else {
                    android.widget.Toast.makeText(
                        this,
                        "Senha incorreta",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.BLACK)
    }


    
    private fun FixScreen(){
        startLockTask()
    }

    fun outScreen(){
        stopLockTask()

        if (dpm.isDeviceOwnerApp(packageName)){
            dpm.setStatusBarDisabled(adminComponentName, false)
        }

        if (dpm.isDeviceOwnerApp(packageName)){
            dpm.setLockTaskPackages(adminComponentName, arrayOf())
        }
        finishAffinity();
    }


    override fun onResume() {
        super.onResume()
        loadApps()
    }
}