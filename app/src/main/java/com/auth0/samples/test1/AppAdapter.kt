package com.auth0.samples.test1

import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(
    private val apps: List<ApplicationInfo>,
    private val selectedApps: MutableSet<String>,
    private val onAppClick: (ApplicationInfo, Boolean) -> Unit,
    private val useGridLayout: Boolean = false
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.app_icon)
        val name: TextView = view.findViewById(R.id.app_name)
        val checkBox: CheckBox? = view.findViewById<CheckBox?>(R.id.app_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        // Usa o layout de grade se useGridLayout for true, senão usa o layout de lista padrão
        val layoutId = if (useGridLayout) {
            R.layout.item_app_grid
        } else {
            R.layout.item_app
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        val pm = holder.itemView.context.packageManager

        holder.icon.setImageDrawable(app.loadIcon(pm))
        holder.name.text = app.loadLabel(pm).toString()

        // Configura o CheckBox apenas se ele estiver visível/necessário (modo lista)
        holder.checkBox?.let { checkbox ->
            // Remove o listener antigo para evitar chamadas indesejadas durante a reciclagem
            checkbox.setOnCheckedChangeListener(null)
            
            checkbox.isChecked = selectedApps.contains(app.packageName)
            
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                onAppClick(app, isChecked)
            }
        }

        holder.itemView.setOnClickListener {
            if (useGridLayout) {
                // No modo grade, o clique abre o app (ignora o checkbox)
                onAppClick(app, false)
            } else {
                // No modo lista, o clique alterna o checkbox
                holder.checkBox?.let {
                    it.isChecked = !it.isChecked
                }
            }
        }
    }

    override fun getItemCount() = apps.size
}