package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class LoginActivity : AppCompatActivity() {
    private var startX: Float = 0f
    private var endX: Float = 0f
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentLocaleString = LocaleManager.getCurrentLocale(this)
        LocaleManager.updateResources(this, Locale(currentLocaleString))

        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        val loginEditText = findViewById<EditText>(R.id.editTextLogin)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val registerButton = findViewById<TextView>(R.id.textViewRegister)

        registerButton.setOnClickListener {
            swipeRight()
        }

        val spinnerLanguage = findViewById<Spinner>(R.id.spinnerLanguage)
        val languages = arrayOf("Русский", "English")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapter

        val currentLangIndex = if (currentLocaleString == "ru") 0 else 1
        spinnerLanguage.setSelection(currentLangIndex)

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val lang = if (position == 0) "ru" else "en"
                LocaleManager.setLocale(this@LoginActivity, lang)

                updateUIWithStrings(
                    R.string.login,
                    R.string.login_hint,
                    R.string.password_hint,
                    R.string.login_button,
                    R.string.register_prompt
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val loginButton = findViewById<Button>(R.id.buttonLogin)
        loginButton.setOnClickListener {
            val login = loginEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (dbHelper.isUserValid(login, password)) {
                val user = dbHelper.getAllUsers().find { it.first == login }
                val name = user?.second

                val intent = Intent(this, MainActivity::class.java)
                if (name != null) {
                    intent.putExtra("NAME", name)
                } else {
                    Log.e("LoginActivity", "User name not found for login: $login")
                    Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show()
            }
        }

        window.decorView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    endX = event.x
                    when {
                        endX > startX -> swipeRight()
                        startX > endX -> swipeLeft()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun swipeRight() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_right)
        window.decorView.startAnimation(animation)

        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
        finish()
    }

    private fun swipeLeft() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_left)
        window.decorView.startAnimation(animation)

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun updateUIWithStrings(
        loginText: Int,
        loginHint: Int,
        passwordHint: Int,
        loginButtonText: Int,
        registerPrompt: Int
    ) {
        findViewById<TextView>(R.id.textViewLogin).text = getString(loginText)
        findViewById<EditText>(R.id.editTextLogin).hint = getString(loginHint)
        findViewById<EditText>(R.id.editTextPassword).hint = getString(passwordHint)
        findViewById<Button>(R.id.buttonLogin).text = getString(loginButtonText)
        findViewById<TextView>(R.id.textViewRegister).text = getString(registerPrompt)
    }
}