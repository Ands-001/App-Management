package com.auth0.samples.test1

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_change_password)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Alterar Senha"

//        createNewPassword()

        val oldPassET = findViewById<EditText>(R.id.old_password)
        val nPassET = findViewById<EditText>(R.id.nPass)
        val confirmNPassET = findViewById<EditText>(R.id.confirm_nPass)
        val saveNPassBtn = findViewById<Button>(R.id.save_nPass)
        val cancelBtn = findViewById<Button>(R.id.cancel_btn)

        saveNPassBtn.setOnClickListener {
            val oldPass = oldPassET.text.toString()
            val nPass = nPassET.text.toString()
            val confirmNPass = confirmNPassET.text.toString()

            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val currentStorePass = sharedPref.getString("new_admin_password", "1234")

            if (oldPass != currentStorePass){
                toast("Senha atual incorreta")
            }

            if (nPass.length < 6){
                toast("Senha muito curta, o mínimo são 6 caracteres")
            }else if (nPass != confirmNPass){
                toast("As senhas não correspondem")
            }else {
                saveNewPassword(nPass)
                finish()
            }
        }

        cancelBtn.setOnClickListener { finish() }
    }
    private fun saveNewPassword(password: String){
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPref.edit().putString("new_admin_password", password).apply()
        toast("Senha altereda com sucesso")
    }

    private fun toast(message: String){
        makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

