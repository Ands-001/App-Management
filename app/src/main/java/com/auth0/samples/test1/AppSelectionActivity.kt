package com.auth0.samples.test1

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AppSelectionActivity : AppCompatActivity() {
    private lateinit var selectedApps: MutableSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_selection)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Selecionar Aplicativos"

        selectedApps = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getStringSet("selected_apps", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.packageName != packageName }
            .sortedBy { it.loadLabel(pm).toString() }

        val adapter = AppAdapter(
            apps = apps,
            selectedApps = selectedApps,
            onAppClick = { app, isSelected ->
                if (isSelected) {
                    selectedApps.add(app.packageName)
                } else {
                    selectedApps.remove(app.packageName)
                }
            },
            useGridLayout = false
        )
        recyclerView.adapter = adapter

        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putStringSet("selected_apps", selectedApps)
                .apply()
            finish()
        }

        val cancelButton = findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}