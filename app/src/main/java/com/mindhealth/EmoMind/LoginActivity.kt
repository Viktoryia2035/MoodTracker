package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private var startX: Float = 0f
    private var endX: Float = 0f
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this, LocaleManager.getCurrentLocale(this))
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        val loginEditText = findViewById<EditText>(R.id.editTextLogin)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        val spinnerLanguage = findViewById<Spinner>(R.id.spinnerLanguage)

        val languages = resources.getStringArray(R.array.languages)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerLanguage.adapter = adapter

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                if (view == null) return

                val selectedLanguage = when (position) {
                    0 -> "ru"
                    1 -> "en"
                    else -> "ru"
                }

                if (LocaleManager.getCurrentLocale(this@LoginActivity) != selectedLanguage) {
                    LocaleManager.setLocale(this@LoginActivity, selectedLanguage)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        loginButton.setOnClickListener {
            val login = loginEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (login.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_login), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_password), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.checkUser(login, password)) {
                val name = dbHelper.getAllUsers().find { it.first == login }?.second

                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("LOGIN", login)
                    apply()
                }

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("LOGIN", login)
                if (name != null) {
                    intent.putExtra("NAME", name)
                }
                startActivity(intent)
                finish()
            }
        }

        val registerPromptTextView = findViewById<TextView>(R.id.textViewRegister)
        registerPromptTextView.setOnClickListener {
            swipeRight()
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

        val loginIntent = Intent(this, RegisterActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
}