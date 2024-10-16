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

        // Получаем логин пользователя из Intent
        val login = intent.getStringExtra("LOGIN")
        Log.d("RedactorProfileActivity", "Login: $login")

        // Найдем поля для имени, логина и пароля
        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val loginEditText = findViewById<EditText>(R.id.editTextText) // Поле для логина
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)

        // Загрузим данные пользователя по логину и отобразим их
        login?.let { userLogin ->
            val name = databaseHelper.getUsername(userLogin) // Получаем имя пользователя
            val password = databaseHelper.getPassword(userLogin) // Получаем пароль

            // Устанавливаем данные в EditText
            nameEditText.setText(name ?: "")
            loginEditText.setText(userLogin) // Устанавливаем логин
            passwordEditText.setText(password ?: "")
        }

        // Обрабатываем нажатие кнопки "Сохранить"
        val saveButton = findViewById<Button>(R.id.buttonCreateAccount)
        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString()
            val newPassword = passwordEditText.text.toString()
            val newLogin = loginEditText.text.toString() // Get the new login from the EditText

            // Обновляем данные пользователя в базе данных
            login?.let { userLogin -> // This is the old login
                val isUpdated = databaseHelper.updateUser(userLogin, newLogin, newName, newPassword)

                if (isUpdated) {
                    Toast.makeText(this, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show()
                    finish() // Закрываем активность после успешного обновления
                } else {
                    Toast.makeText(this, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
