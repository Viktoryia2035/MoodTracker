package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RedactorProfileActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redactor_profile)

        databaseHelper = DatabaseHelper(this)

        val login = intent.getStringExtra("LOGIN")
        Log.d("RedactorProfileActivity", "Login: $login")

        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val loginEditText = findViewById<EditText>(R.id.editTextText)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)

        login?.let { userLogin ->
            val name = databaseHelper.getUsername(userLogin)
            val password = databaseHelper.getPassword(userLogin)

            nameEditText.setText(name ?: "")
            loginEditText.setText(userLogin)
            passwordEditText.setText(password ?: "")
        }

        val saveButton = findViewById<Button>(R.id.buttonCreateAccount)
        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString()
            val newPassword = passwordEditText.text.toString()
            val newLogin = loginEditText.text.toString()

            login?.let { userLogin ->
                val isUpdated = databaseHelper.updateUser(userLogin, newLogin, newName, newPassword)

                if (isUpdated) {
                    Toast.makeText(this, getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.profile_update_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
