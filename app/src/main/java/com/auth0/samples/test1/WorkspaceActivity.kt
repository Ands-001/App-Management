package com.auth0.samples.test1

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

class WorkspaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        loadApps()
    }

    private fun loadApps() {
        val selectedApps = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getStringSet("selected_apps", mutableSetOf()) ?: mutableSetOf()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 4)

        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { selectedApps.contains(it.packageName) }

        val adapter = AppAdapter(
            apps = apps,
            selectedApps = mutableSetOf(),
            onAppClick = { app, _ ->
                val intent = pm.getLaunchIntentForPackage(app.packageName)
                if (intent != null) {
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
                showAdminPasswordDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAdminPasswordDialog() {
        val editText = EditText(this)
        editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        AlertDialog.Builder(this)
            .setTitle("Acesso Administrativo")
            .setMessage("Digite a senha")
            .setView(editText)
            .setPositiveButton("Entrar") { dialog, _ ->
                val password = editText.text.toString()
                if (password == "1234") {
                    startActivity(Intent(this, AppSelectionActivity::class.java))
                } else {
                    // Senha incorreta
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
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadApps()
    }
}