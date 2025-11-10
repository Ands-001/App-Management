package com.auth0.samples.test1

import com.auth0.samples.test1.R
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class AppSelectionActivity : AppCompatActivity() {
    private lateinit var selectedApps: MutableSet<String>
    private lateinit var allApps: List<ApplicationInfo>
    private lateinit var adapter: AppAdapter
    private lateinit var searchEditText: EditText
    private lateinit var clearSearchButton: ImageView
    private lateinit var infoText: TextView
    private lateinit var recyclerView: RecyclerView
    private val TAG = "AppSelectionActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_selection)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Selecionar Aplicativos"
        selectedApps = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getStringSet("selected_apps", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        recyclerView = findViewById(R.id.recycler_view)
        searchEditText = findViewById(R.id.search_edit_text)
        clearSearchButton = findViewById(R.id.clear_search_button)
        infoText = findViewById(R.id.info_text)

        setupRecyclerView()
        setupSearch()
        updateInfoText()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        val pm = packageManager
        allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter {appInfo ->
                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 ||
                        (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
            }
            .filter { it.packageName != packageName }
            .sortedBy { it.loadLabel(pm).toString().lowercase() }

        adapter = AppAdapter(
            apps = allApps,
            selectedApps = selectedApps,
            onAppClick = { app, isSelected ->
                if (isSelected) {
                    selectedApps.add(app.packageName)
                    Log.d(TAG, "App adicionado: ${app.packageName}")
                } else {
                    selectedApps.remove(app.packageName)
                    Log.d(TAG, "App removido: ${app.packageName}")
                }
                updateInfoText()
                Log.d(TAG, "Total selecionados: ${selectedApps.size}")
            },
            useGridLayout = false
        )
        recyclerView.adapter = adapter

        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            Log.d(TAG, "Salvando ${selectedApps.size} apps no SharedPreferences...")

            val success = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putStringSet("selected_apps", HashSet(selectedApps))
                .commit()

            if (success) {
                Log.d(TAG, "Apps salvos!")
                android.widget.Toast.makeText(
                    this,
                    "${selectedApps.size} apps salvos!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.e(TAG, "Erro ao salvar")
                android.widget.Toast.makeText(
                    this,
                    "Erro ao salvar!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }

            finish()
        }
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            Log.d(TAG, "Cancelado - não salvou")
            finish()
        }
    }
    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterApps(s.toString())
                clearSearchButton.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        clearSearchButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                    as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }
    }
    private fun filterApps(query: String) {
        val pm = packageManager
        val filteredApps = if (query.isEmpty()) {
            allApps
        } else {
            allApps.filter { app ->
                val appName = app.loadLabel(pm).toString()
                val packageName = app.packageName

                appName.contains(query, ignoreCase = true) ||
                        packageName.contains(query, ignoreCase = true)
            }
        }
        Log.d(TAG, "Pesquisa: '$query' - ${filteredApps.size} apps encontrados")
        adapter = AppAdapter(
            apps = filteredApps,
            selectedApps = selectedApps,
            onAppClick = { app, isSelected ->
                if (isSelected) {
                    selectedApps.add(app.packageName)
                } else {
                    selectedApps.remove(app.packageName)
                }
                updateInfoText()
            },
            useGridLayout = false
        )
        recyclerView.adapter = adapter
        if (query.isEmpty()) {
            updateInfoText()
        } else {
            infoText.text = "${filteredApps.size} aplicativo(s) encontrado(s)"
        }
    }
    private fun updateInfoText() {
        val count = selectedApps.size
        infoText.text = when (count) {
            0 -> "Nenhum aplicativo selecionado"
            1 -> "1 aplicativo selecionado"
            else -> "$count aplicativos selecionados"
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}