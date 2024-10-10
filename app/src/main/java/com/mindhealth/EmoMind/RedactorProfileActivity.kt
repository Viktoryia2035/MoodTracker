package com.mindhealth.EmoMind

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class RedactorProfileActivity : AppCompatActivity() {
    private var startX: Float = 0f
    private var endX: Float = 0f
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redactor_profile)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get passed data (username)
        val username: String? = intent.getStringExtra("LOGIN")

        // Retrieve existing user data from the database
        username?.let {
            val user = dbHelper.getAllUsers().firstOrNull { userData ->
                userData.first == username
            }

            // Populate fields with existing user data
            user?.let { userData ->
                val (userLogin, userName) = userData
                findViewById<EditText>(R.id.editTextText).setText(userLogin)
                findViewById<EditText>(R.id.editTextPassword).setText(dbHelper.getPassword(userLogin))
                findViewById<EditText>(R.id.editTextName).setText(userName)
            }
        }

        // Handle save button click
        val saveButton: Button = findViewById(R.id.buttonCreateAccount)
        saveButton.setOnClickListener {
            val newUsername: String = findViewById<EditText>(R.id.editTextText).text.toString()
            val newPassword: String = findViewById<EditText>(R.id.editTextPassword).text.toString()
            val name: String = findViewById<EditText>(R.id.editTextName).text.toString()

            // Update user data in the database
            dbHelper.updateUser(name, newUsername, newPassword, null)

            // Navigate to profile screen with updated data
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("LOGIN", newUsername)
            startActivity(intent)
            finish()
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
