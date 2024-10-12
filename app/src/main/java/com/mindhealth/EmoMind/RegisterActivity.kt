package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class RegisterActivity : AppCompatActivity() {
    private var startX: Float = 0f
    private var endX: Float = 0f
    private lateinit var nativeLib: NativeLib
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextName: EditText
    private lateinit var editTextText: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var createAccountButton: Button

    @SuppressLint("ClickableViewAccessibility")
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
            val name = editTextName.text.toString().trim()
            val username = editTextText.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nativeLib.isUserValid(username, password)) {
                Toast.makeText(this, getString(R.string.username_exists), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!dbHelper.addUser(username, password, name)) {
                Toast.makeText(this, getString(R.string.error_adding_user), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("NAME", name)
                apply()
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("NAME", name)
            startActivity(intent)
            finish()
        }

        val textViewLogin = findViewById<TextView>(R.id.textViewLogin)
        textViewLogin.setOnClickListener {
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