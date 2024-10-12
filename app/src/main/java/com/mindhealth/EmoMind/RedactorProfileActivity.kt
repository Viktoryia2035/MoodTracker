package com.mindhealth.EmoMind

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RedactorProfileActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private var login: String? = null
    private var startX: Float = 0f
    private var endX: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redactor_profile)

        databaseHelper = DatabaseHelper(this)

        // Получаем логин пользователя из Intent
        login = intent.getStringExtra("LOGIN")

        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val saveButton = findViewById<Button>(R.id.buttonCreateAccount)

        // Получаем текущие данные пользователя
        login?.let { userLogin ->
            val currentPassword = databaseHelper.getPassword(userLogin)
            val currentName = databaseHelper.getAllUsers().find { it.first == userLogin }?.second

            // Устанавливаем текущие значения в поля редактирования
            currentName?.let { nameEditText.setText(it) }
            currentPassword?.let { passwordEditText.setText(it) }
        }

        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString()
            val newPassword = passwordEditText.text.toString()

            // Обновляем данные пользователя
            login?.let { userLogin ->
                val isUpdated = databaseHelper.updateUser(userLogin, newName, newPassword)

                if (isUpdated) {
                    Toast.makeText(this, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Handle swipe gestures
        window.decorView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    endX = event.x
                    if (endX > startX) {
                        swipeRight()  // Swipe right
                    } else if (startX > endX) {
                        swipeLeft()   // Swipe left
                    }
                    true
                }
                else -> false
            }
        }
    }

    // Swipe left
    private fun swipeLeft() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_left)
        window.decorView.startAnimation(animation)

        val profileIntent = Intent(this, ProfileActivity::class.java)
        profileIntent.putExtra("LOGIN", intent.getStringExtra("LOGIN"))
        startActivity(profileIntent)
        finish()
    }

    // Swipe right
    private fun swipeRight() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_right)
        window.decorView.startAnimation(animation)

        val profileIntent = Intent(this, ProfileActivity::class.java)
        profileIntent.putExtra("LOGIN", intent.getStringExtra("LOGIN"))
        startActivity(profileIntent)
        finish()
    }
}
