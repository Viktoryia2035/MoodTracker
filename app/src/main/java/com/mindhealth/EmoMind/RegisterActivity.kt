package com.mindhealth.EmoMind

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class RegisterActivity : AppCompatActivity() {
    private lateinit var nativeLib: NativeLib
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextName: EditText
    private lateinit var editTextText: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var createAccountButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        nativeLib = NativeLib()
        dbHelper = DatabaseHelper(this)

        editTextName = findViewById(R.id.editTextName)
        editTextText = findViewById(R.id.editTextText)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        createAccountButton = findViewById(R.id.buttonCreateAccount)

        createAccountButton.setOnClickListener {
            val name = editTextName.text.toString()
            val username = editTextText.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            if (password != confirmPassword) {
                Toast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка на существование имени пользователя
            if (nativeLib.isUserValid(username, password)) {
                Toast.makeText(this, getString(R.string.username_exists), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Добавление нового пользователя в базу данных
            if (!dbHelper.addUser(username, password, name)) {
                Toast.makeText(this, getString(R.string.error_adding_user), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Переход к следующему экрану
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("NAME", name) // Ensure this is of type String
            startActivity(intent)
            finish()
        }
    }

    private fun swipeLeft() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_left)
        window.decorView.startAnimation(animation)

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun swipeRight() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_right)
        window.decorView.startAnimation(animation)

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    override fun attachBaseContext(newBase: Context) {
        val appContext = LocaleManager.getCurrentLocale(newBase)
        val locale = Locale(appContext)
        super.attachBaseContext(LocaleManager.changeLocale(newBase, locale))
    }
}
